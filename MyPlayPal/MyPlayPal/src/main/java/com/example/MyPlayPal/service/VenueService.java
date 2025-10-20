package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateVenueRequest;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.dto.CourtDto; // <-- Assuming you have a DTO for Court details

import java.util.List;

public interface VenueService {
    VenueDto createVenue(CreateVenueRequest req);
    VenueDto getById(Long id);
    List<VenueDto> listByCity(String city);
    List<VenueDto> listAllVenues();

    /**
     * ✅ NEW METHOD: Retrieves details for a specific court/sport by its ID.
     * This method is required to display the court name and details on the booking page.
     */
    CourtDto getCourtById(Long courtId);
}