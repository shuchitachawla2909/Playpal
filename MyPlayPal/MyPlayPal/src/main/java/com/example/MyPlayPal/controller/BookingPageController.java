package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.service.CourtService;
import com.example.MyPlayPal.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingPageController {

    @Autowired
    private CourtService courtService;

    @Autowired
    private VenueService venueService;

    // This handles links like /booking?courtId=1&venueId=5
    @GetMapping("/booking")
    public String showBookingPage(@PathVariable Long courtId,
                                  @PathVariable Long venueId,
                                  Model model) {
        CourtDto court = courtService.getById(courtId);
        VenueDto venue = venueService.getById(venueId);

        model.addAttribute("court", court);
        model.addAttribute("venue", venue);

        return "booking";  // this loads booking.html (Thymeleaf view)
    }
}
