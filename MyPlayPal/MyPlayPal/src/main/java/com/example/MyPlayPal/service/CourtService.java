package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateCourtRequest;
import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.dto.CourtSlotDto;
import com.example.MyPlayPal.model.Court;

import java.util.List;

public interface CourtService {
    CourtDto createCourt(CreateCourtRequest req);
    CourtDto getById(Long id);
    List<CourtDto> listByVenue(Long venueId);
}

