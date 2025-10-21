package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.dto.CourtDto; // Assuming you have a Court DTO
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

        model.addAttribute("venues", venues);
        return "venues";
    }

    @GetMapping("/venues/{id}")
    public String venueDetails(@PathVariable("id") Long venueId, Model model) {
        // Renamed variable to venueId for clarity
        VenueDto venueDetail = venueService.getById(venueId);

        if (venueDetail == null) {
            return "redirect:/venues"; // Redirect if venue not found
        }
        model.addAttribute("venue", venueDetail);
        return "venue-detail";
    }

    @GetMapping("/venues/{venueId}/book/{courtId}")
    public String showBookingPage(
            @PathVariable Long venueId,
            @PathVariable Long courtId,
            Model model
    ) {
        // 1. Fetch Venue details
        VenueDto venue = venueService.getById(venueId);

        // 2. Fetch Court details (Assuming a method to get a specific court/sport)
        CourtDto court = venueService.getCourtById(courtId);

        if (venue == null || court == null) {
            // Handle error: redirect back if resources are missing
            return "redirect:/venues";
        }

        model.addAttribute("venue", venue);
        model.addAttribute("court", court);
        return "booking"; // Renders booking.html
    }
}