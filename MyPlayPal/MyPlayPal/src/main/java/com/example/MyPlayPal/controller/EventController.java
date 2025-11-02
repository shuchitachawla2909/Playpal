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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    private final EventMapper eventMapper;

    /**
     * NEW ENDPOINT: Calculate venue booking cost and prepare payment data
     * Called before redirecting to payment page
     */
    @PostMapping("/prepare-payment")
    @Transactional(readOnly = true) // ✅ Add @Transactional to keep session open
    public Map<String, Object> prepareEventPayment(@RequestBody Map<String, Object> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get selected slot IDs
        List<Integer> slotIds = (List<Integer>) request.get("slotIds");
        if (slotIds == null || slotIds.isEmpty()) {
            throw new RuntimeException("At least one slot must be selected");
        }

        // Convert Integer to Long
        List<Long> slotIdLongs = slotIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        // Fetch slots from database
        List<CourtSlot> slots = courtSlotRepository.findAllById(slotIdLongs);
        if (slots.size() != slotIds.size()) {
            throw new RuntimeException("One or more slots not found");
        }

        // Calculate total venue booking cost
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CourtSlot slot : slots) {
            // ✅ Force load of Court and Venue within transaction
            BigDecimal rate = slot.getCourt().getHourlyRate();
            if (rate != null) {
                totalAmount = totalAmount.add(rate);
            }
        }

        // Get first slot and force load venue/court data
        CourtSlot firstSlot = slots.get(0);

        // ✅ Access nested properties to force Hibernate to load them
        String venueName = firstSlot.getCourt().getVenue().getVenuename();
        String courtName = firstSlot.getCourt().getCourtname();
        String bookingDate = firstSlot.getStartTime().toLocalDate().toString();

        // Format slot times for display (e.g., "07:00 - 08:00, 08:00 - 09:00")
        String slotTimes = slots.stream()
                .map(slot -> {
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    return slot.getStartTime().format(timeFormatter) + " - " +
                            slot.getEndTime().format(timeFormatter);
                })
                .collect(Collectors.joining(", "));

        // Build response map (using HashMap to avoid Map.of() limit of 10 entries)
        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount", totalAmount);
        response.put("venueName", venueName);
        response.put("courtName", courtName);
        response.put("bookingDate", bookingDate);
        response.put("selectedSlots", slotTimes);
        response.put("eventName", request.get("eventName"));
        response.put("maxPlayers", request.get("maxPlayers"));
        response.put("description", request.get("description"));
        response.put("skillLevelRequired", request.get("skillLevelRequired"));
        response.put("entryFee", request.get("entryFee"));
        response.put("slotIds", slotIds);

        return response;
    }

    /**
     * CREATE EVENT AFTER PAYMENT SUCCESS
     * Called from frontend after Razorpay payment succeeds
     */
    @PostMapping("/create-with-venue")
    @Transactional
    public EventResponse createEventWithVenue(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User organizer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ Handle both Integer and Long types for slotIds
            List<Long> slotIdLongs;
            Object slotIdsObj = request.get("slotIds");

            if (slotIdsObj instanceof List) {
                List<?> rawList = (List<?>) slotIdsObj;
                slotIdLongs = rawList.stream()
                        .map(obj -> {
                            if (obj instanceof Integer) {
                                return ((Integer) obj).longValue();
                            } else if (obj instanceof Long) {
                                return (Long) obj;
                            } else {
                                return Long.parseLong(obj.toString());
                            }
                        })
                        .collect(Collectors.toList());
            } else {
                throw new RuntimeException("Invalid slotIds format");
            }

            if (slotIdLongs.isEmpty()) {
                throw new RuntimeException("At least one slot must be selected");
            }

            // Fetch slots
            List<CourtSlot> slots = courtSlotRepository.findAllById(slotIdLongs);
            if (slots.size() != slotIdLongs.size()) {
                throw new RuntimeException("One or more slots not found. Expected: " + slotIdLongs.size() + ", Found: " + slots.size());
            }

            // Check if slots are available
            for (CourtSlot slot : slots) {
                if (slot.getStatus() != CourtSlot.SlotStatus.AVAILABLE) {
                    throw new RuntimeException("Slot " + slot.getId() + " is not available");
                }
            }

            // Calculate total amount
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CourtSlot slot : slots) {
                BigDecimal rate = slot.getCourt().getHourlyRate();
                if (rate != null) {
                    totalAmount = totalAmount.add(rate);
                }
            }

            // Mark slots as booked
            for (CourtSlot slot : slots) {
                slot.setStatus(CourtSlot.SlotStatus.BOOKED);
                courtSlotRepository.save(slot);
            }

            // ✅ Parse entry fee safely
            BigDecimal entryFee;
            Object entryFeeObj = request.get("entryFee");
            if (entryFeeObj instanceof Number) {
                entryFee = BigDecimal.valueOf(((Number) entryFeeObj).doubleValue());
            } else {
                entryFee = new BigDecimal(entryFeeObj.toString());
            }

            // ✅ Parse maxPlayers safely
            Integer maxPlayers;
            Object maxPlayersObj = request.get("maxPlayers");
            if (maxPlayersObj instanceof Integer) {
                maxPlayers = (Integer) maxPlayersObj;
            } else {
                maxPlayers = Integer.parseInt(maxPlayersObj.toString());
            }

            // Create event
            Event event = Event.builder()
                    .eventName((String) request.get("eventName"))
                    .maxPlayers(maxPlayers)
                    .description((String) request.get("description"))
                    .skillLevelRequired((String) request.get("skillLevelRequired"))
                    .entryFee(entryFee)
                    .organizer(organizer)
                    .slots(slots)
                    .currentPlayers(0)
                    .status(Event.EventStatus.CONFIRMED)
                    .totalAmount(totalAmount)
                    .build();

            Event savedEvent = eventService.createEvent(event);

            // Force load necessary data before returning
            savedEvent.getOrganizer().getUsername();

            return eventMapper.toEventResponse(savedEvent);

        } catch (Exception e) {
            // ✅ Log the full error
            e.printStackTrace();
            throw new RuntimeException("Failed to create event: " + e.getMessage(), e);
        }
    }
    /**
     * CREATE BASIC EVENT (without venue booking)
     */
    @PostMapping
    @Transactional
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
        return eventMapper.toEventResponse(savedEvent);
    }

    /**
     * GET EVENT BY ID
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public EventResponse getEvent(@PathVariable Long id) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return eventMapper.toEventResponse(event);
    }

    /**
     * GET ALL EVENTS
     */
    @GetMapping
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents().stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());
    }

    /**
     * GET EVENTS BY ORGANIZER
     */
    @GetMapping("/organizer/{organizerId}")
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByOrganizer(@PathVariable Long organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));
        return eventService.getEventsByOrganizer(organizer).stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());
    }

    /**
     * SEARCH EVENTS BY NAME
     */
    @GetMapping("/search")
    @Transactional(readOnly = true)
    public List<EventResponse> searchEvents(@RequestParam String name) {
        return eventService.searchEventsByName(name).stream()
                .map(eventMapper::toEventResponse)
                .collect(Collectors.toList());
    }

    /**
     * CANCEL EVENT
     */
    @PostMapping("/{id}/cancel")
    @Transactional
    public void cancelEvent(@PathVariable Long id) {
        eventService.cancelEvent(id);
    }

}