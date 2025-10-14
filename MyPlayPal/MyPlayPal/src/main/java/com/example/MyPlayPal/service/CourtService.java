package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateCourtRequest;
import com.example.MyPlayPal.dto.CourtDto;

import java.util.List;

public interface CourtService {
    CourtDto createCourt(CreateCourtRequest req);
    CourtDto getById(Long id);
    List<CourtDto> listByVenue(Long venueId);
}

