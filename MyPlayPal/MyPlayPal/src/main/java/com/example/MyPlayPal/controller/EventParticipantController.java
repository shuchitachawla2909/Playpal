package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class EventParticipantController {

    private final EventParticipantService participantService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    // Join an event
    @PostMapping("/join")
    public EventParticipant joinEvent(@RequestParam Long eventId, @RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return participantService.joinEvent(eventId, user);
    }

    // Leave an event
    @PostMapping("/leave")
    public void leaveEvent(@RequestParam Long eventId, @RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        participantService.leaveEvent(eventId, user);
    }

    // Get participants of an event
    @GetMapping("/event/{eventId}")
    public List<EventParticipant> getParticipants(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return participantService.getParticipantsByEvent(event);
    }

    // Get events a user has joined
    @GetMapping("/user/{userId}")
    public List<EventParticipant> getEventsByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return participantService.getEventsByUser(user);
    }
}
