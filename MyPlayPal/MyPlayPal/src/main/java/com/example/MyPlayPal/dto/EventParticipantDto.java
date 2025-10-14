package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipantDto {
    private Long id;
    private Long eventId;
    private Long userId;
    private Instant joinDate;
    private String status;
}

