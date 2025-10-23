package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;


    @Override
    public Event createEvent(Event event) {
        // Initially, currentPlayers = 0
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

//    @Override
//    public List<Event> getUpcomingEvents(LocalDateTime now) {
//        return eventRepository.findByStartTimeAfter(now);
//    }

    @Override
    public List<Event> getAvailableEvents() {
        // Get all events where currentPlayers < maxPlayers
        return eventRepository.findByCurrentPlayersLessThan(Integer.MAX_VALUE); // or filter in service
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

            // Optionally, cancel all participants
            List<EventParticipant> participants = participantRepository.findByEvent(event);
            for (EventParticipant participant : participants) {
                participant.setStatus(EventParticipant.ParticipantStatus.CANCELLED);
            }
            eventRepository.save(event);
        }
    }
}
