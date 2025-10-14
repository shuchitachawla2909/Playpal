package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateEventRequest;
import com.example.MyPlayPal.dto.EventDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.Sport;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.SportRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.repository.VenueRepository;
import com.example.MyPlayPal.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SportRepository sportRepository;
    private final VenueRepository venueRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, SportRepository sportRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.sportRepository = sportRepository;
        this.venueRepository = venueRepository;
    }

    @Override
    @Transactional
    public EventDto createEvent(CreateEventRequest req) {
        User organizer = userRepository.findById(req.getOrganizerId()).orElseThrow(() -> new ResourceNotFoundException("Organizer not found"));
        Sport sport = sportRepository.findById(req.getSportId()).orElseThrow(() -> new ResourceNotFoundException("Sport not found"));
        Venue venue = venueRepository.findById(req.getVenueId()).orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        if (req.getEndTime().isBefore(req.getStartTime()) || req.getEndTime().equals(req.getStartTime())) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }

        if (req.getMaxPlayers() != null && req.getMaxPlayers() <= 0) {
            throw new IllegalArgumentException("Max players must be greater than zero");
        }
        Event e = Event.builder()
                .eventName(req.getEventName())
                .organizer(organizer)
                .sport(sport)
                .venue(venue)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .maxPlayers(req.getMaxPlayers())
                .description(req.getDescription())
                .skillLevelRequired(req.getSkillLevelRequired())
                .entryFee(req.getEntryFee())
                .build();

        Event saved = eventRepository.save(e);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getById(Long id) {
        Event e = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return toDto(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> listBySport(Long sportId) {
        return eventRepository.findBySportId(sportId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private EventDto toDto(Event e) {
        return EventDto.builder()
                .id(e.getId())
                .eventName(e.getEventName())
                .organizerId(e.getOrganizer() == null ? null : e.getOrganizer().getId())
                .sportId(e.getSport() == null ? null : e.getSport().getId())
                .venueId(e.getVenue() == null ? null : e.getVenue().getId())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .maxPlayers(e.getMaxPlayers())
                .description(e.getDescription())
                .skillLevelRequired(e.getSkillLevelRequired())
                .entryFee(e.getEntryFee())
                .build();
    }
}
