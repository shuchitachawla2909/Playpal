package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateCourtRequest;
import com.example.MyPlayPal.dto.CourtDto;
import com.example.MyPlayPal.service.CourtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courts")
public class CourtController {

    @Autowired
    private CourtService courtService;

    @GetMapping
    public ResponseEntity<List<CourtDto>> listByVenue(@RequestParam(value = "venueId", required = false) Long venueId) {
        if (venueId != null) {
            return ResponseEntity.ok(courtService.listByVenue(venueId));
        }
        // optional: return all courts if you implement a listAll
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtDto> getCourt(@PathVariable Long id) {
        return ResponseEntity.ok(courtService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CourtDto> createCourt(@Valid @RequestBody CreateCourtRequest request) {
        return ResponseEntity.ok(courtService.createCourt(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourt(@PathVariable Long id) {
        return ResponseEntity.ok("Delete court endpoint not implemented");
    }
}

