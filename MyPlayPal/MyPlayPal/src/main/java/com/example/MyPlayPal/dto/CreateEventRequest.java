package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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

    @NotNull(message = "maxPlayers is required")
    @Min(1)
    private Integer maxPlayers;

    @Size(max = 500)
    private String description;

    private String skillLevelRequired;

    @DecimalMin(value = "0.0", inclusive = true, message = "entryFee must be non-negative")
    private BigDecimal entryFee;

    private List<Long> slotIds; // IDs of CourtSlots to associate with this event
}
