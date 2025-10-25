package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import com.example.MyPlayPal.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepo;
    private final EventService eventService;
    private final EventParticipantService participantService;

    @GetMapping("/profile")
    public String profilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = auth.getName();
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        user.setPassword(null);

        List<Event> eventsCreated = eventService.getEventsByOrganizer(user);
        List<Event> eventsJoined = participantService.getEventsJoinedByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("eventsCreated", eventsCreated);
        model.addAttribute("eventsJoined", eventsJoined);
        return "profile";
    }
}
