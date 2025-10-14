package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.EventParticipantDto;
import com.example.MyPlayPal.service.EventParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/event-participants")
public class EventParticipantController {

    @Autowired
    private EventParticipantService participantService;

    @PostMapping("/join")
    public ResponseEntity<EventParticipantDto> joinEvent(
            @RequestParam Long eventId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(participantService.joinEvent(eventId, userId));
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<EventParticipantDto>> listByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(participantService.listByEvent(eventId));
    }
}
