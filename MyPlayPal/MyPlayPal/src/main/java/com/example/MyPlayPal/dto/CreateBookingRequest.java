package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    // --- Authentication (Placeholder, should be derived from Principal) ---
    @NotNull(message = "userId is required")
    private Long userId;

    // --- Slot/Court Details (Needed for database lookup and calculation) ---
    // NOTE: We are replacing 'slotId' with a combination of Court ID, Date, and Time
    @NotNull(message = "Court ID is required")
    private Long courtId;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @NotBlank(message = "Start time is required")
    private String startTime; // e.g., "19:00"

    // --- Payment Details ---
    @NotNull(message = "Total amount paid is required")
    private BigDecimal totalAmount;

    @NotBlank(message = "Payment reference ID is required")
    private String paymentRefId;
}