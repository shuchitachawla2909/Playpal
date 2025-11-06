package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtSlotDto {
    private Long id;
    private Long courtId;
    private String courtname;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}

