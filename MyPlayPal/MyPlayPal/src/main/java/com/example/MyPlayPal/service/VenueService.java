package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateVenueRequest;
import com.example.MyPlayPal.dto.VenueDto;

import java.util.List;

public interface VenueService {
    VenueDto createVenue(CreateVenueRequest req);
    VenueDto getById(Long id);
    List<VenueDto> listByCity(String city);
    List<VenueDto> listAllVenues();
}
