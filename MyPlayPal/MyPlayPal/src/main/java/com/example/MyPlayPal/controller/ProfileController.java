package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepo;

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
        user.setPassword(null); // hide password
        model.addAttribute("user", user);
        return "profile"; // templates/profile.html
    }
}
