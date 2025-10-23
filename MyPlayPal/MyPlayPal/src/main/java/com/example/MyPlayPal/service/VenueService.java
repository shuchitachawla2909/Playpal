package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.*;

import java.util.List;

public interface VenueService {
    VenueDto createVenue(CreateVenueRequest req);
    VenueDto getById(Long id);
    List<VenueDto> listByCity(String city);
    List<VenueDto> listAllVenues();

    CourtDto getCourtById(Long courtId);

    List<VenueDto> listVenuesForCurrentManager();  // Show only venues of logged-in manager
    void addCourtToVenue(Long venueId, CreateCourtRequest request);
    List<SportDto> getAllSports();

    VenueDto updateVenue(Long venueId, VenueDto updatedVenue);
    void deleteVenue(Long venueId);
    void updateCourt(Long courtId, CourtDto updatedCourt);
}

