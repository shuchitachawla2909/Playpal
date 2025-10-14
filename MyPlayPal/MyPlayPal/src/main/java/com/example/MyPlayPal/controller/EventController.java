package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateEventRequest;
import com.example.MyPlayPal.dto.EventDto;
import com.example.MyPlayPal.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping("/by-sport/{sportId}")
    public ResponseEntity<List<EventDto>> listBySport(@PathVariable Long sportId) {
        return ResponseEntity.ok(eventService.listBySport(sportId));
    }
}

