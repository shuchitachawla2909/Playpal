package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateCourtSlotRequest;
import com.example.MyPlayPal.dto.CourtSlotDto;
import com.example.MyPlayPal.dto.VenueSlotResponse;
import com.example.MyPlayPal.service.CourtSlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class CourtSlotController {

    @Autowired
    private CourtSlotService slotService;

    @PostMapping
    public ResponseEntity<CourtSlotDto> createSlot(@Valid @RequestBody CreateCourtSlotRequest request) {
        return ResponseEntity.ok(slotService.createSlot(request));
    }

    @GetMapping("/by-court")
    public ResponseEntity<List<CourtSlotDto>> findSlots(
            @RequestParam Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(slotService.findSlotsByCourtAndRange(courtId, from, to));
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<VenueSlotResponse>> getAvailableSlotsByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(slotService.getAvailableSlotsByVenue(venueId));
    }

}
