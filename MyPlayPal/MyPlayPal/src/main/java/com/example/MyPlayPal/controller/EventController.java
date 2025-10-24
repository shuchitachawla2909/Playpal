package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;

    @PostMapping("/create-with-venue")
    public Event createEventWithVenue(@RequestBody Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = new Event();
        event.setEventName((String) request.get("eventName"));
        event.setMaxPlayers((Integer) request.get("maxPlayers"));
        event.setDescription((String) request.get("description"));
        event.setSkillLevelRequired((String) request.get("skillLevelRequired"));
        event.setEntryFee(new BigDecimal(request.get("entryFee").toString()));
        event.setOrganizer(organizer);

        return eventService.createEvent(event);
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event, @RequestParam Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        event.setOrganizer(organizer);
        return eventService.createEvent(event);
    }

    // Get event by ID
    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    // Get all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // Get events by organizer
    @GetMapping("/organizer/{organizerId}")
    public List<Event> getEventsByOrganizer(@PathVariable Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        return eventService.getEventsByOrganizer(organizer);
    }

    // Search events by name
    @GetMapping("/search")
    public List<Event> searchEvents(@RequestParam String name) {
        return eventService.searchEventsByName(name);
    }

    // Cancel event
    @PostMapping("/{id}/cancel")
    public void cancelEvent(@PathVariable Long id) {
        eventService.cancelEvent(id);
    }

    // Get upcoming events
//    @GetMapping("/upcoming")
//    public List<Event> getUpcomingEvents() {
//        return eventService.getUpcomingEvents(LocalDateTime.now());
//    }
}
