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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/court-slots")
public class CourtSlotController {

    @Autowired
    private CourtSlotService courtSlotService;

    @PostMapping
    public ResponseEntity<CourtSlotDto> createSlot(@Valid @RequestBody CreateCourtSlotRequest request) {
        return ResponseEntity.ok(courtSlotService.createSlot(request));
    }

    @GetMapping("/by-court")
    public ResponseEntity<List<CourtSlotDto>> findSlots(
            @RequestParam Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(courtSlotService.findSlotsByCourtAndRange(courtId, from, to));
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<VenueSlotResponse>> getAvailableSlotsByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(courtSlotService.getAvailableSlotsByVenue(venueId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<CourtSlotDto>> getAvailableSlotsForCourtAndDate(
            @RequestParam("courtId") Long courtId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<CourtSlotDto> slots = courtSlotService.getAvailableSlotsForCourtAndDate(courtId, date);
        return ResponseEntity.ok(slots);
    }
}
