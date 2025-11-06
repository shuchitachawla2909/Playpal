package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    // ✅ Automatically set from Principal in controller
    private Long userId;

    // ✅ Court ID for reference
    @NotNull(message = "Court ID is required")
    private Long courtId;

    // ✅ Slot ID selected by user (single slot)
    @NotNull(message = "Slot ID is required")
    private Long slotId;

    // ✅ Total amount for this booking (can be computed in backend too)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    // optional: if you generate payment reference later
    private String paymentRefId;
}
