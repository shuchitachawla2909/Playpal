package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtDto {
    private Long id;
    private String courtname;
    private Long venueId;
    private Long sportId;
    private BigDecimal hourlyRate;
    private Boolean isBookable;
    private String sportImageUrl;
}

