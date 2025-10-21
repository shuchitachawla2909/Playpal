package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private Long userId;
    private Long slotId;
    private Long courtId;
    private Instant bookingDate;
    private String status;
    private BigDecimal totalAmount;
}

