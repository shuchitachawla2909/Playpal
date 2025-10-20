package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.UserSport;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.UserSportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PlayerController {

    private final UserSportService userSportService;
    private final UserRepository userRepository;

    @GetMapping("/players")
    public String showOtherPlayers(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElse(null);

        if (currentUser == null) {
            return "redirect:/login";
        }

        // Fetch all UserSport entries except current user
        List<UserSport> allUserSports = userSportService.getAllOtherPlayers(currentUser.getId());

        // Group by User
        Map<User, List<UserSport>> playersGrouped = allUserSports.stream()
                .collect(Collectors.groupingBy(UserSport::getUser));

        model.addAttribute("playersGrouped", playersGrouped);

        return "players";
    }
}
