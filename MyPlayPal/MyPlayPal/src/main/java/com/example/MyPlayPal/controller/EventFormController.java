package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EventFormController {

    private final EventParticipantService participantService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @PostMapping("/api/participants/join")
    public String joinEvent(@RequestParam Long eventId, @RequestParam Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Event not found"));
            participantService.joinEvent(eventId, user);
            return "redirect:/events/" + eventId + "?joined=true";
        } catch (Exception e) {
            return "redirect:/events/" + eventId + "?error=" + e.getMessage();
        }
    }
}
