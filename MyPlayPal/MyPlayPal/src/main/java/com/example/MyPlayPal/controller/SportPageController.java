package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.service.SportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/games/{id}")
    public String showGameDetailPage(@PathVariable Long id, Model model) {
        var sport = sportService.getById(id);
        var venues = sportService.getVenuesBySport(id);
        var events = sportService.getAvailableEventsBySport(id);

        model.addAttribute("sport", sport);
        model.addAttribute("venues", venues);
        model.addAttribute("events", events);
        return "game-detail"; // maps to game-detail.html
    }

    @PostMapping("/user-sports/{id}")
    public String handleUserSport(@PathVariable Long id) {
        // do something with the sport (like save a booking, etc.)
        return "redirect:/games/" + id; // redirect after POST
    }

}

