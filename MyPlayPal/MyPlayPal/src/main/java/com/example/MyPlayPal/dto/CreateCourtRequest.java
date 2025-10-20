package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourtRequest {
    @NotBlank(message = "courtName is required")
    @Size(max = 150)
    private String courtName;

    @NotNull(message = "venueId is required")
    private Long venueId;

    @NotNull(message = "sportId is required")
    private Long sportId;

    @NotNull(message = "hourlyRate is required")
    @Positive(message = "hourlyRate must be positive")
    private BigDecimal hourlyRate;

    // optional; defaults to true in service if null
    private Boolean isBookable;
}

