package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.CourtSlot;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.VenueRepository;
import com.example.MyPlayPal.service.EmailService;
import com.example.MyPlayPal.service.EventService;
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
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final VenueRepository venueRepository; // For venue-based event fetching

    @Autowired
    private EmailService emailService;


    @Override
    public Event createEvent(Event event) {
        // Initialize event defaults
        event.setCurrentPlayers(0);
        event.setStatus(Event.EventStatus.PENDING);

        Event savedEvent = eventRepository.save(event);

        // --- Send email to the organizer ---
        String toEmail = savedEvent.getOrganizer().getEmail();
        String subject = "✅ Your Event Has Been Created Successfully!";

        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                + "<h2 style='color: #2E86C1;'>Event Confirmation</h2>"
                + "<p>Hi <b>" + savedEvent.getOrganizer().getUsername() + "</b>,</p>"
                + "<p>Your event <b>" + savedEvent.getEventName() + "</b> has been created successfully.</p>"
                + "<table style='border-collapse: collapse; width: 100%;'>"
                + "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Date:</b></td>"
                + "<td style='padding: 8px; border: 1px solid #ddd;'>" + savedEvent.getBookingDate() + "</td></tr>"
                + "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Max Players:</b></td>"
                + "<td style='padding: 8px; border: 1px solid #ddd;'>" + savedEvent.getMaxPlayers() + "</td></tr>"
                + "<tr><td style='padding: 8px; border: 1px solid #ddd;'><b>Entry Fee:</b></td>"
                + "<td style='padding: 8px; border: 1px solid #ddd;'>₹"
                + (savedEvent.getEntryFee() != null ? savedEvent.getEntryFee() : "0") + "</td></tr>"
                + "</table>"
                + "<p style='margin-top: 20px;'>Thank you for organizing your event with us!</p>"
                + "<p style='color: #888;'>– Avenue Events Team</p>"
                + "</body></html>";

        try {
            emailService.sendBookingConfirmation(toEmail, subject, htmlContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return savedEvent;
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

            // Update court slots back to AVAILABLE
            if (event.getSlots() != null && !event.getSlots().isEmpty()) {
                for (CourtSlot slot : event.getSlots()) {
                    if (slot.getStatus() == CourtSlot.SlotStatus.BOOKED) {
                        slot.setStatus(CourtSlot.SlotStatus.AVAILABLE);
                    }
                }
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
