package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class EventParticipantController {

    private final EventParticipantService participantService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @PostMapping("/join")
    public String joinEvent(@RequestParam Long eventId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // ✅ Prevent organizer from joining again
        if (event.getOrganizer().getId().equals(user.getId())) {
            return "redirect:/events/" + eventId + "?isOrganizer=true";
        }

        try {
            participantService.joinEvent(eventId, user);
            return "redirect:/events/" + eventId + "?joined=true";
        } catch (RuntimeException e) {
            // ✅ Handle "already joined" gracefully
            if ("User already joined this event".equals(e.getMessage())) {
                return "redirect:/events/" + eventId + "?alreadyJoined=true";
            } else if ("Event is full".equals(e.getMessage())) {
                return "redirect:/events/" + eventId + "?full=true";
            }
            throw e; // Let other exceptions propagate
        }
    }

    @PostMapping("/leave/{eventId}")
    public String leaveEvent(@PathVariable Long eventId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        participantService.leaveEvent(eventId, user);
        return "redirect:/events/" + eventId + "?left=true";
    }

    @GetMapping("/event/{eventId}")
    @ResponseBody
    public List<EventParticipant> getParticipants(@PathVariable Long eventId) {
        return participantService.getParticipantsByEventId(eventId);
    }

    @GetMapping("/my-events")
    @ResponseBody
    public List<EventParticipant> getMyEvents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return participantService.getEventsByUser(user);
    }
}
