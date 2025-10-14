package com.example.MyPlayPal.web;

import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
@RequiredArgsConstructor
public class CurrentUserAdvice {

    private final UserRepository userRepo;

    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            model.addAttribute("authenticated", true);
            model.addAttribute("currentUsername", username);

            userRepo.findByUsername(username).ifPresent(u -> {
                // Do not expose sensitive fields
                u.setPassword(null);
                model.addAttribute("currentUser", u);
            });
        } else {
            model.addAttribute("authenticated", false);
        }
    }
}
