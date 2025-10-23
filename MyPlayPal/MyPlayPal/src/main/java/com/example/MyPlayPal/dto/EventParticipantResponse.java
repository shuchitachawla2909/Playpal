package com.example.MyPlayPal.dto;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantResponse {

    private Long id;

    private Long userId;
    private String username;

    private Long eventId;
    private String eventName;

    private Instant joinDate;
    private String status; // JOINED, PENDING, CANCELLED
}
