package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateCourtSlotRequest;
import com.example.MyPlayPal.dto.CourtSlotDto;
import com.example.MyPlayPal.dto.VenueSlotResponse;
import com.example.MyPlayPal.service.CourtSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/court-slots")
public class CourtSlotController {

    @Autowired
    private CourtSlotService courtSlotService;

    /**
     * ✅ Create new slot — handles duplicate slot exception
     */
    @PostMapping
    public ResponseEntity<?> createSlot(@Valid @RequestBody CreateCourtSlotRequest request) {
        try {
            CourtSlotDto createdSlot = courtSlotService.createSlot(request);
            return ResponseEntity.ok(createdSlot);
        } catch (IllegalStateException e) {
            // 🔴 Slot already exists
            return ResponseEntity.badRequest().body("Slot already exists for this court and time.");
        } catch (Exception e) {
            // ⚠️ Unexpected error
            return ResponseEntity.internalServerError().body("Error creating slot: " + e.getMessage());
        }
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
