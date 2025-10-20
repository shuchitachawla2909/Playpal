package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.service.SportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SportPageController {

    private final SportService sportService;

    public SportPageController(SportService sportService) {
        this.sportService = sportService;
    }

    @GetMapping("/games")
    public String showGamesPage(Model model) {
        // Fetch all sports from DB
        model.addAttribute("sports", sportService.listAll());
        return "games"; // Thymeleaf template name: games.html
    }
}

