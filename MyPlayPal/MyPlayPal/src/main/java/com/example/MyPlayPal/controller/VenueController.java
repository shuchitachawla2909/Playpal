package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateVenueRequest;
import com.example.MyPlayPal.dto.VenueDto;
import com.example.MyPlayPal.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.ui.Model; // REMOVED: Not needed in REST Controller
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

    // REMOVED CONFLICTING METHOD: @GetMapping("/venues") public String showVenuesPage(Model model) { ... }

    @GetMapping
    public ResponseEntity<List<VenueDto>> listAll(@RequestParam(value = "city", required = false) String city) {
        if (city != null) {
            return ResponseEntity.ok(venueService.listByCity(city));
        } else {
            return ResponseEntity.ok(venueService.listAllVenues()); // Updated to call a proper listAll
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