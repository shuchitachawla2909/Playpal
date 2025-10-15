package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.ReviewDto;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.service.ReviewService; // <-- Import
import com.example.MyPlayPal.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class VenuePageController {

    private final VenueService venueService;
    private final ReviewService reviewService; // <-- NEW Field

    // UPDATE CONSTRUCTOR: Inject both services
    public VenuePageController(VenueService venueService, ReviewService reviewService) {
        this.venueService = venueService;
        this.reviewService = reviewService; // <-- Initialize
    }

    @GetMapping("/venues")
    public String getVenuesPage(Model model) {
        List<VenueDto> venues = venueService.listAllVenues();
        // NOTE: If you want to show average rating on the list page, you'd need to loop and enrich here.
        model.addAttribute("venues", venues);
        return "venues";
    }

    @GetMapping("/venue/{id}") // Best practice to use descriptive path
    public String venueDetails(@PathVariable Long id, Model model) {
        // 1. Get base venue details
        VenueDto venueDetail = venueService.getById(id);

        // 2. Fetch and calculate review data
        List<ReviewDto> reviews = reviewService.listByVenue(id);
        Double averageRating = reviewService.getAverageRating(id);

        // 3. ENRICH the VenueDto object (or add separately to the model)
        venueDetail.setReviews(reviews);
        venueDetail.setAverageRating(averageRating);

        // 4. Add the enriched DTO to the model
        model.addAttribute("venue", venueDetail);

        // Ensure this matches your template file name
        return "venue-detail";
    }
}
