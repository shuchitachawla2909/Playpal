package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.service.EmailService;
import com.example.MyPlayPal.service.EventParticipantService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private EmailService emailService;

    @Override
    public EventParticipant joinEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Prevent organizer from joining their own event
        if (event.getOrganizer().getId().equals(user.getId())) {
            throw new RuntimeException("Organizer cannot join their own event");
        }

        // Prevent duplicate participation
        participantRepository.findByEventAndUser(event, user)
                .ifPresent(p -> { throw new RuntimeException("User already joined this event"); });

        // Prevent joining if event is already full
        if (event.getCurrentPlayers() >= event.getMaxPlayers()) {
            throw new RuntimeException("Event is full");
        }

        // Create participant
        EventParticipant participant = EventParticipant.builder()
                .event(event)
                .user(user)
                .status(EventParticipant.ParticipantStatus.JOINED)
                .build();

        // Update players count
        event.setCurrentPlayers(event.getCurrentPlayers() + 1);
        eventRepository.save(event);

        EventParticipant savedParticipant = participantRepository.save(participant);

        // --- Send email to participant ---
        String toEmail = user.getEmail();
        String subject = "🎉 You’ve Successfully Joined the Event!";

        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<h2 style='color: #28B463;'>Event Participation Confirmation</h2>"
                + "<p>Hi <b>" + user.getUsername() + "</b>,</p>"
                + "<p>You have successfully joined the event <b>" + event.getEventName() + "</b>.</p>"
                + "<table style='border-collapse: collapse; width: 100%;'>"
                + "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Date:</b></td>"
                + "<td style='padding: 8px; border: 1px solid #ddd;'>" + event.getBookingDate() + "</td></tr>"
                + "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Organizer:</b></td>"
                + "<td style='padding: 8px; border: 1px solid #ddd;'>" + event.getOrganizer().getUsername() + "</td></tr>"
                + "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Max Players:</b></td>"
                + "<td style='padding: 8px; border: 1px solid #ddd;'>" + event.getMaxPlayers() + "</td></tr>"
                + "</table>"
                + "<p style='margin-top: 20px;'>We hope you enjoy the event!</p>"
                + "<p style='color: #888;'>– Avenue Events Team</p>"
                + "</body></html>";

        try {
            emailService.sendBookingConfirmation(toEmail, subject, htmlContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return savedParticipant;
    }


    @Override
    public void leaveEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        EventParticipant participant = participantRepository.findByEventAndUser(event, user)
                .orElseThrow(() -> new RuntimeException("User not part of this event"));

        // Set participant status to CANCELLED instead of deleting
        participant.setStatus(EventParticipant.ParticipantStatus.CANCELLED);
        participantRepository.save(participant);

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
