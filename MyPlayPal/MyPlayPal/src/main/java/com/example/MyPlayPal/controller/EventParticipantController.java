package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class EventParticipantController {

    private final EventParticipantService participantService;
    private final UserRepository userRepository;

    @PostMapping("/join/{eventId}")
    public EventParticipant joinEvent(@PathVariable Long eventId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return participantService.joinEvent(eventId, user);
    }

    @PostMapping("/leave/{eventId}")
    public void leaveEvent(@PathVariable Long eventId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        participantService.leaveEvent(eventId, user);
    }

    @GetMapping("/event/{eventId}")
    public List<EventParticipant> getParticipants(@PathVariable Long eventId) {
        return participantService.getParticipantsByEventId(eventId);
    }

    @GetMapping("/my-events")
    public List<EventParticipant> getMyEvents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return participantService.getEventsByUser(user);
    }
}
