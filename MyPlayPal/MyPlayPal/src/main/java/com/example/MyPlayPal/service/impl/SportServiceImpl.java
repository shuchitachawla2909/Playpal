package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateSportRequest;
import com.example.MyPlayPal.dto.SportDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Sport;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.repository.*;
import com.example.MyPlayPal.service.SportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;
    private final CourtRepository courtRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    public SportServiceImpl(
            SportRepository sportRepository,
            CourtRepository courtRepository,
            VenueRepository venueRepository,
            EventRepository eventRepository
    ) {
        this.sportRepository = sportRepository;
        this.courtRepository = courtRepository;
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public SportDto createSport(CreateSportRequest req) {
        Sport s = Sport.builder().sportname(req.getSportname()).build();
        Sport saved = sportRepository.save(s);
        return SportDto.builder().id(saved.getId()).sportname(saved.getSportname()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public SportDto getById(Long id) {
        Sport s = sportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sport not found"));
        return SportDto.builder().id(s.getId()).sportname(s.getSportname()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SportDto> listAll() {
        return sportRepository.findAll().stream()
                .map(s -> SportDto.builder()
                        .id(s.getId())
                        .sportname(s.getSportname())
                        .sportImageUrl(s.getSportImageUrl()) // new
                        .build())
                .collect(Collectors.toList());
    }

    // ✅ NEW METHOD 1: Get all venues that offer courts for this sport
    @Override
    @Transactional(readOnly = true)
    public List<Venue> getVenuesBySport(Long sportId) {
        List<Court> courts = courtRepository.findBySportId(sportId);
        return courts.stream()
                .map(Court::getVenue)
                .distinct()
                .collect(Collectors.toList());
    }

    // ✅ NEW METHOD 2: Get all available events for this sport
    @Override
    @Transactional(readOnly = true)
    public List<Event> getAvailableEventsBySport(Long sportId) {
        // ✅ Fetch only confirmed events for this sport that still have available slots
        return eventRepository.findBySportIdAndCurrentPlayersLessThanMaxPlayersAndStatus(
                sportId,
                Event.EventStatus.CONFIRMED
        );
    }
}

