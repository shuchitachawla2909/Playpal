package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.BookingDto;
import com.example.MyPlayPal.dto.CreateBookingRequest;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.*;
import com.example.MyPlayPal.repository.*;
import com.example.MyPlayPal.service.BookingService;
import com.example.MyPlayPal.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final PaymentTransactionRepository paymentRepo;
    private final CourtRepository courtRepo;
    private final CourtSlotRepository slotRepo;

    @Autowired
    private EmailService emailService;

    public BookingServiceImpl(
            CourtSlotRepository slotRepo,
            BookingRepository bookingRepo,
            UserRepository userRepo,
            PaymentTransactionRepository paymentRepo,
            CourtRepository courtRepo) {
        this.slotRepo = slotRepo;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.paymentRepo = paymentRepo;
        this.courtRepo = courtRepo;
    }

    @Override
    @Transactional
    public BookingDto createBooking(CreateBookingRequest req) {
        // --- 1. Validate User ---
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + req.getUserId()));

        // --- 2. Validate and Lock Slot ---
        LocalTime requestedStartTime = req.getStartTime();
        LocalDateTime startDateTime = req.getBookingDate().atTime(requestedStartTime);
        LocalDateTime endDateTime = startDateTime.plusHours(1);

        CourtSlot slot = slotRepo.findByCourtIdAndStartTimeAndEndTime(req.getCourtId(), startDateTime, endDateTime)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found for the specified time and court."));

        if (slot.getStatus() != CourtSlot.SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Slot is already booked or unavailable.");
        }

        // --- 3. Lock slot to prevent concurrent booking ---
        slot.setStatus(CourtSlot.SlotStatus.BOOKED);
        slotRepo.save(slot);

        // --- 4. Calculate total booking cost ---
        BigDecimal rate = slot.getCourt().getHourlyRate() == null
                ? BigDecimal.ZERO
                : slot.getCourt().getHourlyRate();

        long minutes = java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = rate.multiply(hours);

        // --- 5. Create Booking ---
        Booking booking = Booking.builder()
                .user(user)
                .slot(slot)
                .bookingDate(Instant.now())
                .status(Booking.BookingStatus.CONFIRMED)
                .totalAmount(totalAmount)
                .build();

        Booking savedBooking = bookingRepo.save(booking);

        // --- 6. Record Payment Transaction ---
        PaymentTransaction transaction = PaymentTransaction.builder()
                .user(user)
                .booking(savedBooking)
                .amount(totalAmount)
                .timestamp(Instant.now())
                .status(PaymentTransaction.PaymentStatus.SUCCESS)
                .referenceId(req.getPaymentRefId())
                .build();

        paymentRepo.save(transaction);

        // sending email part
        try {
            String htmlTemplate = new String(
                    Objects.requireNonNull(
                            getClass().getClassLoader().getResourceAsStream("templates/booking-confirmation-template.html")
                    ).readAllBytes()
            );

            htmlTemplate = htmlTemplate
                    .replace("{{username}}", user.getUsername())
                    .replace("{{courtName}}", slot.getCourt().getCourtname())
                    .replace("{{bookingDate}}", req.getBookingDate().toString())
                    .replace("{{startTime}}", req.getStartTime().toString())
                    .replace("{{endTime}}", req.getStartTime().plusHours(1).toString())
                    .replace("{{amount}}", totalAmount.toString());

            emailService.sendBookingConfirmation(
                    user.getEmail(),
                    "Booking Confirmed: " + slot.getCourt().getCourtname(),
                    htmlTemplate
            );
        } catch (Exception e) {
            e.printStackTrace(); // optional logging
        }


        // --- 7. Convert to DTO and Return ---
        return toDto(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long id) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> listByUser(Long userId) {
        return bookingRepo.findByUserIdOrderByBookingDateDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // --- Private helper method ---
    private BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .courtId(booking.getSlot().getCourt().getId())
                .bookingDate(booking.getBookingDate())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUserId(Long userId) {
        // ✅ Use the injected userRepo instead of the class name
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Use the injected bookingRepo instead of the class name
        List<Booking> bookings = bookingRepo.findByUser(user);

        // ✅ The booking model refers to a CourtSlot (slot), not directly to a Court
        return bookings.stream()
                .map(b -> BookingDto.builder()
                        .id(b.getId())
                        .courtId(b.getSlot().getCourt().getId())
                        .courtname(b.getSlot().getCourt().getCourtname()) // ✅ fixed case
                        .bookingDate(b.getBookingDate())
                        .startTime(b.getSlot().getStartTime())
                        .endTime(b.getSlot().getEndTime())
                        .totalAmount(b.getTotalAmount())
                        .status(b.getStatus().toString())
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public BookingDto cancelBooking(Long bookingId, Long userId) {
        // 1️⃣ Validate user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 2️⃣ Fetch booking and check ownership
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to cancel this booking.");
        }

        // 3️⃣ Check current booking status
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled.");
        }

        // 4️⃣ Soft cancel the booking
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepo.save(booking);

        // 5️⃣ Free up the slot again for rebooking
        CourtSlot slot = booking.getSlot();
        slot.setStatus(CourtSlot.SlotStatus.AVAILABLE);
        slotRepo.save(slot);

        // 6️⃣ (Optional) Update payment status if integration exists
        if (booking.getPayment() != null) {
            booking.getPayment().setStatus(PaymentTransaction.PaymentStatus.REFUNDED);
            paymentRepo.save(booking.getPayment());
        }

        // 7️⃣ Return updated DTO
        return toDto(booking);
    }


}
