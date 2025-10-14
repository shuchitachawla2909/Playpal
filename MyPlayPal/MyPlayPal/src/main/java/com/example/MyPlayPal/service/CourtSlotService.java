package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateCourtSlotRequest;
import com.example.MyPlayPal.dto.CourtSlotDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CourtSlotService {
    CourtSlotDto createSlot(CreateCourtSlotRequest req);
    List<CourtSlotDto> findSlotsByCourtAndRange(Long courtId, LocalDateTime from, LocalDateTime to);
}
