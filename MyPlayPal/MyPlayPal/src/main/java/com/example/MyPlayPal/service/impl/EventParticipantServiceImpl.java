package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;

    @Override
    public EventParticipant joinEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 🚫 1️⃣ Prevent organizer from joining their own event
        if (event.getOrganizer().getId().equals(user.getId())) {
            throw new RuntimeException("Organizer cannot join their own event");
        }

        // 🚫 2️⃣ Prevent duplicate participation
        participantRepository.findByEventAndUser(event, user)
                .ifPresent(p -> { throw new RuntimeException("User already joined this event"); });

        // 🚫 3️⃣ Prevent joining if event is already full
        if (event.getCurrentPlayers() >= event.getMaxPlayers()) {
            throw new RuntimeException("Event is full");
        }

        // ✅ 4️⃣ Create new participant entry
        EventParticipant participant = EventParticipant.builder()
                .event(event)
                .user(user)
                .status(EventParticipant.ParticipantStatus.JOINED)
                .build();

        // ✅ 5️⃣ Update current players count
        event.setCurrentPlayers(event.getCurrentPlayers() + 1);
        eventRepository.save(event);

        return participantRepository.save(participant);
    }

    @Override
    public void leaveEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        EventParticipant participant = participantRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new RuntimeException("User not part of this event"));

        // Remove participant
        participantRepository.delete(participant);

        // Update player count
        event.setCurrentPlayers(event.getCurrentPlayers() - 1);
        eventRepository.save(event);
    }

    @Override
    public List<EventParticipant> getParticipantsByEvent(Event event) {
        return participantRepository.findByEvent(event);
    }

    @Override
    public List<EventParticipant> getEventsByUser(User user) {
        return participantRepository.findByUser(user);
    }

    @Override
    public boolean isUserParticipant(Event event, User user) {
        return participantRepository.findByEventAndUser(event, user).isPresent();
    }

    @Override
    public long countParticipantsByStatus(Event event, EventParticipant.ParticipantStatus status) {
        return participantRepository.countByEventAndStatus(event, status);
    }

    @Override
    public List<EventParticipant> getParticipantsByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return getParticipantsByEvent(event);
    }

    @Override
    public List<Event> getEventsJoinedByUser(User user) {
        return participantRepository.findEventsJoinedByUser(user);
    }

}
