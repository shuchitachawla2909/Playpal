package com.example.MyPlayPal.controller;


import com.example.MyPlayPal.dto.CreateVenueRequest;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping("/venues")
    public String showVenuesPage(Model model) {
        List<VenueDto> venues = venueService.listAllVenues();
        model.addAttribute("venues", venues);
        return "venues"; // Thymeleaf template name: venues.html
    }

    @GetMapping
    public ResponseEntity<List<VenueDto>> listAll(@RequestParam(value = "city", required = false) String city) {
        if (city != null) {
            return ResponseEntity.ok(venueService.listByCity(city));
        } else {
            // you may add a listAll in service; for now call listByCity with null -> handle in service if implemented
            return ResponseEntity.ok(venueService.listByCity(null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueDto> getVenue(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getById(id));
    }

    @PostMapping
    public ResponseEntity<VenueDto> createVenue(@Valid @RequestBody CreateVenueRequest request) {
        return ResponseEntity.ok(venueService.createVenue(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVenue(@PathVariable Long id) {
        return ResponseEntity.ok("Delete venue endpoint not implemented");
    }
}




