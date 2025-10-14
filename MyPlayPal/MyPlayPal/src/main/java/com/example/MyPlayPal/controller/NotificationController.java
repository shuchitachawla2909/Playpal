package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.NotificationDto;
import com.example.MyPlayPal.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/create")
    public ResponseEntity<NotificationDto> createNotification(@RequestParam Long userId, @RequestParam String message) {
        return ResponseEntity.ok(notificationService.createNotification(userId, message));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<NotificationDto>> listForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.listForUser(userId));
    }
}
