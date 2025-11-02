package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateSportRequest;
import com.example.MyPlayPal.dto.SportDto;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.Venue;

import java.util.List;

public interface SportService {
    SportDto createSport(CreateSportRequest req);
    SportDto getById(Long id);
    List<SportDto> listAll();

    // ➕ New methods for game-details page
    List<Venue> getVenuesBySport(Long sportId);
    List<Event> getAvailableEventsBySport(Long sportId);
}
