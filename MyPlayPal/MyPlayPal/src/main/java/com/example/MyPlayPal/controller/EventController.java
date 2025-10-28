package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.EventResponse;
import com.example.MyPlayPal.mapper.EventMapper;
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
    private final EventMapper eventMapper; // ✅ Inject mapper

    // ✅ CREATE EVENT WITH VENUE
    @PostMapping("/create-with-venue")
    public EventResponse createEventWithVenue(@RequestBody Map<String, Object> request) {
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

        Event savedEvent = eventService.createEvent(event);
        return eventMapper.toEventResponse(savedEvent); // ✅ Use instance method
    }

    // ✅ CREATE BASIC EVENT
    @PostMapping
    public EventResponse createEvent(@RequestBody Map<String, Object> request) {
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

        Event savedEvent = eventService.createEvent(event);
        return eventMapper.toEventResponse(savedEvent); // ✅
    }

    // ✅ GET EVENT BY ID
    @GetMapping("/{id}")
    public EventResponse getEvent(@PathVariable Long id) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return eventMapper.toEventResponse(event);
    }

    // ✅ GET ALL EVENTS
    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());
    }

    // ✅ GET EVENTS BY ORGANIZER
    @GetMapping("/organizer/{organizerId}")
    public List<EventResponse> getEventsByOrganizer(@PathVariable Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        return eventService.getEventsByOrganizer(organizer).stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());
    }

    // ✅ SEARCH EVENTS BY NAME
    @GetMapping("/search")
    public List<EventResponse> searchEvents(@RequestParam String name) {
        return eventService.searchEventsByName(name).stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());
    }

    // ✅ CANCEL EVENT
    @PostMapping("/{id}/cancel")
    public void cancelEvent(@PathVariable Long id) {
        eventService.cancelEvent(id);
    }
}
