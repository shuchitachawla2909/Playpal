package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    // --- Authenticated User ---
    // Derived automatically from the logged-in Principal (not from frontend form)
    // So this will not be included in the booking form, but we keep the field here
    // for internal mapping if needed in the controller.
    private Long userId;

    // --- Court and Slot Information ---
    @NotNull(message = "Court ID is required")
    private Long courtId;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    // --- Payment Information ---
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @NotBlank(message = "Payment reference ID is required")
    private String paymentRefId;
}
