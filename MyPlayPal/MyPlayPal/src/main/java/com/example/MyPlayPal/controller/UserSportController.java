package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Sport;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.UserSport;
import com.example.MyPlayPal.repository.SportRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.repository.UserSportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user-sports")
public class UserSportController {

    private final UserRepository userRepository;
    private final SportRepository sportRepository;
    private final UserSportRepository userSportRepository;

    /**
     * When user clicks "View Details" for a sport,
     * automatically link the user to that sport in the user_sport table.
     */
    @PostMapping("/{sportId}")
    public String addUserSport(@PathVariable Long sportId) {

        // Get current authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If user not logged in → redirect to login page
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        // Fetch user by username
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch sport by ID
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        // Prevent duplicate link between same user and sport
        boolean exists = userSportRepository.existsByUserAndSport(user, sport);
        if (!exists) {
            UserSport userSport = UserSport.builder()
                    .user(user)
                    .sport(sport)
                    .skillLevel("beginner") // default value for now
                    .build();
            userSportRepository.save(userSport);
        }

        // After saving, redirect to that sport’s detail page
        return "redirect:/games/" + sportId;
    }
}
