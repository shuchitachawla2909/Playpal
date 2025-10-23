package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.dto.CreateCourtRequest;
import com.example.MyPlayPal.dto.CreateVenueRequest;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.service.ManagerService;
import com.example.MyPlayPal.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager") // All URLs start with /manager
public class ManagerPageController {

    private final VenueService venueService;
    private final ManagerService managerService;

    public ManagerPageController(VenueService venueService, ManagerService managerService) {
        this.venueService = venueService;
        this.managerService = managerService;
    }

    @ModelAttribute
    public void addCurrentManager(Model model) {
        model.addAttribute("currentManager", managerService.getLoggedInManager());
    }


    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<VenueDto> venues = venueService.listVenuesForCurrentManager();
        model.addAttribute("venues", venues);
        Manager manager = managerService.getLoggedInManager();
        model.addAttribute("currentManager", manager);
        return "manager-dashboard";
    }

    // Show Add Venue Form
    @GetMapping("/add-venue")
    public String showAddVenueForm(Model model) {
        model.addAttribute("venueRequest", new CreateVenueRequest());
        return "add-venue";
    }

    // Handle Add Venue POST
    @PostMapping("/add-venue")
    public String createVenue(@ModelAttribute CreateVenueRequest venueRequest) {
        venueService.createVenue(venueRequest);
        return "redirect:/manager/dashboard";
    }

    // Show Add Court Form
    @GetMapping("/venues/{venueId}/add-court")
    public String showAddCourtForm(@PathVariable Long venueId, Model model) {
        model.addAttribute("courtRequest", new CreateCourtRequest());

        // ✅ Add venue details (for title and form action)
        model.addAttribute("venue", venueService.getById(venueId));

        // ✅ Add list of sports (for the dropdown)
        model.addAttribute("sports", venueService.getAllSports());
        // or use sportService.getAllSports() if you have a separate service

        return "add-court";
    }


    // Handle Add Court POST
    @PostMapping("/venues/{venueId}/add-court")
    public String addCourt(@PathVariable Long venueId,
                           @ModelAttribute CreateCourtRequest courtRequest) {
        venueService.addCourtToVenue(venueId, courtRequest);
        return "redirect:/manager/dashboard";
    }

    // Show manager's venue details
    @GetMapping("/venues/{venueId}")
    public String showVenueDetails(@PathVariable Long venueId, Model model) {
        VenueDto venue = venueService.getById(venueId); // returns DTO with courts
        model.addAttribute("venue", venue);

        // For dropdowns in courts editing
        model.addAttribute("sports", venueService.getAllSports());

        return "manager-venue-details";
    }

    // Handle updating the venue
    @PostMapping("/venues/{venueId}/update")
    public String updateVenue(@PathVariable Long venueId,
                              @ModelAttribute VenueDto updatedVenue) {
        venueService.updateVenue(venueId, updatedVenue);
        return "redirect:/manager/venues/" + venueId;
    }

    // Handle deleting the venue
    @PostMapping("/venues/{venueId}/delete")
    public String deleteVenue(@PathVariable Long venueId) {
        venueService.deleteVenue(venueId);
        return "redirect:/manager/dashboard";
    }


    // Handle updating a court
    @PostMapping("/venues/{venueId}/courts/{courtId}/update")
    public String updateCourt(@PathVariable Long venueId,
                              @PathVariable Long courtId,
                              @ModelAttribute CourtDto updatedCourt) {
        venueService.updateCourt(courtId, updatedCourt); // we will add this method
        return "redirect:/manager/venues/" + venueId;
    }

}
