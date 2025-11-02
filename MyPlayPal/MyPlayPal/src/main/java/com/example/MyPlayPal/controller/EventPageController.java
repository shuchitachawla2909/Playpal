package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.repository.VenueRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import com.example.MyPlayPal.service.EventService;
import com.example.MyPlayPal.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventPageController {

    private final EventService eventService;
    private final EventParticipantService participantService;
    private final VenueService venueService;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    // ✅ 1️⃣ Updated to show only CONFIRMED events
    @GetMapping("/events")
    public String eventsPage(Model model) {
        // ✅ Use your existing method to fetch only CONFIRMED events
        List<Event> events = eventService.getEventsByStatus(Event.EventStatus.CONFIRMED);

        model.addAttribute("events", events);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        model.addAttribute("isLoggedIn", isLoggedIn);

        return "events";
    }

    // ✅ 2️⃣ Keep this as-is (shows event details)
    @GetMapping("/events/{id}")
    public String eventDetailsPage(@PathVariable Long id, Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<EventParticipant> participants = participantService.getParticipantsByEvent(event);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                currentUserId = user.getId();
            }
        }

        model.addAttribute("event", event);
        model.addAttribute("participants", participants);
        model.addAttribute("currentUserId", currentUserId);
        return "event-details";
    }

    // ✅ 3️⃣ Keep event creation methods unchanged
    @GetMapping("/events/create")
    public String createEventPage(Model model) {
        List<VenueDto> venues = venueService.listAllVenues();
        model.addAttribute("venues", venues);
        return "venue-events";
    }

    @GetMapping("/events/create/venue/{venueId}")
    public String createEventAtVenuePage(@PathVariable Long venueId, Model model) {
        model.addAttribute("venue", venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found")));
        return "create-event";
    }
}
