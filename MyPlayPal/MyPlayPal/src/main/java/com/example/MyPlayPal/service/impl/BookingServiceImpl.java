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
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final CourtSlotRepository slotRepo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final PaymentTransactionRepository paymentRepo;

    public BookingServiceImpl(CourtSlotRepository slotRepo,
                              BookingRepository bookingRepo,
                              UserRepository userRepo,
                              PaymentTransactionRepository paymentRepo) {
        this.slotRepo = slotRepo;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    @Transactional
    public BookingDto createBooking(CreateBookingRequest req) {
        User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CourtSlot slot = slotRepo.findByIdForUpdate(req.getSlotId()).orElseThrow(() -> new ResourceNotFoundException("Slot not found"));
        if (slot.getEndTime().isBefore(slot.getStartTime()) || slot.getEndTime().equals(slot.getStartTime())) {
            throw new IllegalArgumentException("Slot end time must be after start time");
        }

        if (!slot.isAvailable()) {
            throw new IllegalStateException("Slot not available");
        }

        slot.setStatus(CourtSlot.SlotStatus.BOOKED);
        slotRepo.save(slot);

        var court = slot.getCourt();
        BigDecimal rate = court.getHourlyRate() == null ? BigDecimal.ZERO : court.getHourlyRate();

        long minutes = java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal total = rate.multiply(hours);

        Booking booking = Booking.builder()
                .user(user)
                .slot(slot)
                .bookingDate(Instant.now())
                .status(Booking.BookingStatus.CONFIRMED)
                .totalAmount(total)
                .build();

        Booking saved = bookingRepo.save(booking);

        PaymentTransaction tx = PaymentTransaction.builder()
                .user(user)
                .booking(saved)
                .amount(total)
                .timestamp(Instant.now())
                .status(PaymentTransaction.PaymentStatus.SUCCESS) // mocked success
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
    public java.util.List<BookingDto> listByUser(Long userId) {
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

