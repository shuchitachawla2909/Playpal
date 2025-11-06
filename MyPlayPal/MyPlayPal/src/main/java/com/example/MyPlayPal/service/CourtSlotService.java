package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateCourtSlotRequest;
import com.example.MyPlayPal.dto.CourtSlotDto;
import com.example.MyPlayPal.dto.VenueSlotResponse;
import com.example.MyPlayPal.model.CourtSlot;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

public interface CourtSlotService {
    CourtSlotDto createSlot(CreateCourtSlotRequest req);
    List<CourtSlotDto> findSlotsByCourtAndRange(Long courtId, LocalDateTime from, LocalDateTime to);
    List<VenueSlotResponse> getAvailableSlotsByVenue(Long venueId);
    List<CourtSlotDto> getAvailableSlotsForCourtAndDate(Long courtId, LocalDate date);
    List<CourtSlot> getSlotsByCourtId(Long courtId);

    void deleteSlotById(Long slotId);
    CourtSlotDto getCourtSlotById(Long slotId);
}
