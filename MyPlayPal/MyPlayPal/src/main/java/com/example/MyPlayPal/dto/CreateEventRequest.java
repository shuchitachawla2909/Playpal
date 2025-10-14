package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
    @NotBlank(message = "eventName is required")
    @Size(max = 200)
    private String eventName;

    @NotNull(message = "organizerId is required")
    private Long organizerId;

    @NotNull(message = "sportId is required")
    private Long sportId;

    @NotNull(message = "venueId is required")
    private Long venueId;

    @NotNull(message = "startTime is required")
    @Future(message = "startTime must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "endTime is required")
    @Future(message = "endTime must be in the future")
    private LocalDateTime endTime;

    @Positive(message = "maxPlayers must be positive")
    private Integer maxPlayers;

    @Size(max = 1000)
    private String description;

    @Pattern(regexp = "Beginner|Intermediate|Advanced|Any", message = "skillLevelRequired must be Beginner/Intermediate/Advanced/Any")
    private String skillLevelRequired;

    @Positive(message = "entryFee must be positive")
    private BigDecimal entryFee;
}

