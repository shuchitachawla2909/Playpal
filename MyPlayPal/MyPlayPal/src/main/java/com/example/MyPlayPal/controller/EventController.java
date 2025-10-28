package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.CourtSlot;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.CourtSlotRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;
    private final CourtSlotRepository courtSlotRepository;

    @PostMapping("/create-with-venue")
    public Event createEventWithVenue(@RequestBody Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Integer> slotIds = (List<Integer>) request.get("slotIds");
        if (slotIds == null || slotIds.isEmpty()) {
            throw new RuntimeException("At least one slot must be selected");
        }

        List<Long> slotIdLongs = slotIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        List<CourtSlot> slots = courtSlotRepository.findAllById(slotIdLongs);
        if (slots.size() != slotIds.size()) {
            throw new RuntimeException("One or more slots not found");
        }

        Event event = Event.builder()
                .eventName((String) request.get("eventName"))
                .maxPlayers((Integer) request.get("maxPlayers"))
                .description((String) request.get("description"))
                .skillLevelRequired((String) request.get("skillLevelRequired"))
                .entryFee(new BigDecimal(request.get("entryFee").toString()))
                .organizer(organizer)
                .slots(slots)
                .currentPlayers(0)
                .status(Event.EventStatus.PENDING)
                .build();

        return eventService.createEvent(event);
    }

    @PostMapping
    public Event createEvent(@RequestBody Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = new Event();
        event.setEventName((String) request.get("eventName"));
        event.setMaxPlayers((Integer) request.get("maxPlayers"));
        event.setDescription((String) request.get("description"));
        event.setSkillLevelRequired((String) request.get("skillLevelRequired"));
        if (request.containsKey("entryFee")) {
            event.setEntryFee(new BigDecimal(request.get("entryFee").toString()));
        }
        event.setOrganizer(organizer);

        return eventService.createEvent(event);
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/organizer/{organizerId}")
    public List<Event> getEventsByOrganizer(@PathVariable Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        return eventService.getEventsByOrganizer(organizer);
    }

    @GetMapping("/search")
    public List<Event> searchEvents(@RequestParam String name) {
        return eventService.searchEventsByName(name);
    }

    @PostMapping("/{id}/cancel")
    public void cancelEvent(@PathVariable Long id) {
        eventService.cancelEvent(id);
    }
}
