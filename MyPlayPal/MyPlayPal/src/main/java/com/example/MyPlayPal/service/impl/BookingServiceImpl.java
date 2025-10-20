package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.BookingDto;
import com.example.MyPlayPal.dto.CreateBookingRequest;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.*;
import com.example.MyPlayPal.repository.*;
import com.example.MyPlayPal.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final CourtSlotRepository slotRepo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final PaymentTransactionRepository paymentRepo;
    private final CourtRepository courtRepo;

    public BookingServiceImpl(CourtSlotRepository slotRepo,
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
        // --- 1. User and Time Validation ---
        User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Convert DTO fields (LocalDate, String) to LocalDateTime
        LocalTime requestedStartTime = LocalTime.parse(req.getStartTime());
        LocalDateTime startDateTime = req.getBookingDate().atTime(requestedStartTime);
        LocalDateTime endDateTime = startDateTime.plusHours(1); // Assuming 1-hour slots

        // --- 2. Find and Lock Slot ---
        // Uses the correctly named repository method: findByCourtIdAndStartTimeAndEndTime
        CourtSlot slot = slotRepo.findByCourtIdAndStartTimeAndEndTime(req.getCourtId(), startDateTime, endDateTime)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found for specified date/time/court."));

        // Check availability (Note: The repository query should ideally filter status too)
        if (!slot.getStatus().equals(CourtSlot.SlotStatus.AVAILABLE)) {
            throw new IllegalStateException("Slot is not available for booking.");
        }

        // --- 3. Process Transaction ---
        slot.setStatus(CourtSlot.SlotStatus.BOOKED);
        slotRepo.save(slot);

        // Calculate Total Amount (Server-side validation of price)
        BigDecimal rate = slot.getCourt().getHourlyRate() == null ? BigDecimal.ZERO : slot.getCourt().getHourlyRate();

        long minutes = java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        BigDecimal calculatedTotal = rate.multiply(hours);

        // --- 4. Create Booking ---
        Booking booking = Booking.builder()
                .user(user)
                .slot(slot)
                .bookingDate(Instant.now())
                .status(Booking.BookingStatus.CONFIRMED)
                .totalAmount(calculatedTotal) // Use the server-calculated total
                .build();

        Booking saved = bookingRepo.save(booking);

        // --- 5. Create Payment Transaction Record ---
        PaymentTransaction tx = PaymentTransaction.builder()
                .user(user)
                .booking(saved)
                .amount(calculatedTotal) // Use the final calculated amount
                .timestamp(Instant.now())
                .status(PaymentTransaction.PaymentStatus.SUCCESS)
                .referenceId(req.getPaymentRefId()) // Field added to PaymentTransaction model
                .build();

        PaymentTransaction savedTx = paymentRepo.save(tx);
        saved.setPayment(savedTx);
        bookingRepo.save(saved);

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long id) {
        Booking b = bookingRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return toDto(b);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> listByUser(Long userId) {
        return bookingRepo.findByUserIdOrderByBookingDateDesc(userId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private BookingDto toDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .userId(b.getUser() == null ? null : b.getUser().getId())
                .slotId(b.getSlot() == null ? null : b.getSlot().getId())
                .bookingDate(b.getBookingDate())
                .status(b.getStatus().name())
                .totalAmount(b.getTotalAmount())
                .build();
    }
}
