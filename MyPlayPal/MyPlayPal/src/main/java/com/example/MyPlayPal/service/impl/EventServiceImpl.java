package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.VenueRepository;
import com.example.MyPlayPal.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final VenueRepository venueRepository; // For venue-based event fetching

    @Override
    public Event createEvent(Event event) {
        // Initialize event defaults
        event.setCurrentPlayers(0);
        event.setStatus(Event.EventStatus.PENDING);
        return eventRepository.save(event);
    }

    @Override
    public Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    @Override
    public List<Event> getEventsByStatus(Event.EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    @Override
    public List<Event> searchEventsByName(String name) {
        return eventRepository.findByEventNameContainingIgnoreCase(name);
    }

    @Override
    public List<Event> getAvailableEvents() {
        // Fetch all events where currentPlayers < maxPlayers
        return eventRepository.findByCurrentPlayersLessThan(Integer.MAX_VALUE);
    }

    @Override
    public List<Event> getAvailableConfirmedEventsBySport(Long sportId) {
        // ✅ Corrected repository method call
        return eventRepository.findBySportIdAndCurrentPlayersLessThanMaxPlayersAndStatus(
                sportId,
                Event.EventStatus.CONFIRMED
        );
    }

    @Override
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void cancelEvent(Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setStatus(Event.EventStatus.CANCELLED);

            // Cancel all participants of this event
            List<EventParticipant> participants = participantRepository.findByEvent(event);
            for (EventParticipant participant : participants) {
                participant.setStatus(EventParticipant.ParticipantStatus.CANCELLED);
            }

            eventRepository.save(event);
        }
    }

    @Override
    public List<Event> getEventsByVenue(Long venueId) {
        // Fetch the venue by ID
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        // Return all events at that venue
        return eventRepository.findByVenue(venue);
    }
}
