package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class VenuePageController {

    private final VenueService venueService;

    public VenuePageController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping("/venues")
    public String getVenuesPage(Model model) {
        List<VenueDto> venues = venueService.listAllVenues();
        model.addAttribute("venues", venues); // Thymeleaf will use ${venues}
        return "venues"; // name of the HTML file in src/main/resources/templates/venues.html
    }

    @GetMapping("/{id}")
    public String venueDetails(@PathVariable Long id, Model model) {
        VenueDto venueDetail = venueService.getById(id);
        model.addAttribute("venue", venueDetail);
        return "venue-detail";
    }
}
