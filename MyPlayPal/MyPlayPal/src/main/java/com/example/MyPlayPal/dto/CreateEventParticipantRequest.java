package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventParticipantRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "eventId is required")
    private Long eventId;
}
