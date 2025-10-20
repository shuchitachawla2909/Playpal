package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ManagerProfileController {

    private final ManagerRepository managerRepo;

    @GetMapping("/manager/profile")
    public String managerProfilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        String managername = auth.getName();
        Manager manager = managerRepo.findByManagername(managername).orElse(null);

        if (manager == null) {
            return "redirect:/login";
        }

        manager.setPassword(null); // hide password before passing to view
        model.addAttribute("manager", manager);
        return "manager-profile"; // templates/manager-profile.html
    }
}

