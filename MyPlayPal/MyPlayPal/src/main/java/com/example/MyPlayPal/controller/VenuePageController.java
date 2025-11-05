package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.ManagerDto;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.dto.ReviewDto;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.service.ManagerService;
import com.example.MyPlayPal.service.VenueService;
import com.example.MyPlayPal.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
public class VenuePageController {

    private final VenueService venueService;
    private final ReviewService reviewService; // Add ReviewService dependency
    private final ManagerService managerService;

    @Autowired
    public VenuePageController(VenueService venueService, ReviewService reviewService, ManagerService managerService) {
        this.venueService = venueService;
        this.reviewService = reviewService; // Initialize reviewService
        this.managerService = managerService;
    }

    @GetMapping("/venues")
    public String getVenuesPage(Model model) {
        List<VenueDto> venues = venueService.listAllVenues();
        model.addAttribute("venues", venues);
        return "venues";
    }

    @GetMapping("/venues/{id}")
    public String getVenueDetail(@PathVariable Long id, Model model, Principal principal) {
        VenueDto venue = venueService.getById(id); // Changed to VenueDto to match service
        List<ReviewDto> reviews = reviewService.listByVenue(id);
        // Fetch manager using managerId
        ManagerDto manager = managerService.getById(venue.getManagerId());

        model.addAttribute("venue", venue);
        model.addAttribute("reviews", reviews);
        model.addAttribute("manager", manager);
        model.addAttribute("authenticated", principal != null);

        return "venue-detail";
    }

    @GetMapping("/venues/{venueId}/book/{courtId}")
    public String showBookingPage(
            @PathVariable Long venueId,
            @PathVariable Long courtId,
            Model model
    ) {
        VenueDto venue = venueService.getById(venueId);
        CourtDto court = venueService.getCourtById(courtId);

        if (venue == null || court == null) {
            return "redirect:/venues";
        }

        model.addAttribute("venue", venue);
        model.addAttribute("court", court);
        return "booking";
    }
}