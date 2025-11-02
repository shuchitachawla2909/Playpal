package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateSportRequest;
import com.example.MyPlayPal.dto.SportDto;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.service.SportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sports")
public class SportController {

    @Autowired
    private SportService sportService;

    // ✅ 1. Get all sports
    @GetMapping
    public ResponseEntity<List<SportDto>> listSports() {
        return ResponseEntity.ok(sportService.listAll());
    }

    // ✅ 2. Get a specific sport by ID
    @GetMapping("/{id}")
    public ResponseEntity<SportDto> getSport(@PathVariable Long id) {
        return ResponseEntity.ok(sportService.getById(id));
    }

    // ✅ 3. Create a new sport
    @PostMapping
    public ResponseEntity<SportDto> createSport(@Valid @RequestBody CreateSportRequest request) {
        return ResponseEntity.ok(sportService.createSport(request));
    }

    // ✅ 4. Get all venues that offer this sport
    @GetMapping("/{sportId}/venues")
    public ResponseEntity<List<Venue>> getVenuesBySport(@PathVariable Long sportId) {
        List<Venue> venues = sportService.getVenuesBySport(sportId);
        return ResponseEntity.ok(venues);
    }

    // ✅ 5. Get all confirmed & available events for this sport
    @GetMapping("/{sportId}/available-events")
    public ResponseEntity<List<Event>> getAvailableEventsBySport(@PathVariable Long sportId) {
        List<Event> events = sportService.getAvailableEventsBySport(sportId);
        return ResponseEntity.ok(events);
    }

    // (optional) Delete — not implemented
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSport(@PathVariable Long id) {
        return ResponseEntity.ok("Delete sport endpoint not implemented");
    }
}
