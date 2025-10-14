package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateUserSportRequest;
import com.example.MyPlayPal.dto.UserSportDto;
import com.example.MyPlayPal.service.UserSportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-sports")
public class UserSportController {

    @Autowired
    private UserSportService userSportService;

    @PostMapping
    public ResponseEntity<UserSportDto> addUserSport(@Valid @RequestBody CreateUserSportRequest request) {
        return ResponseEntity.ok(userSportService.addUserSport(request));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<UserSportDto>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userSportService.listByUser(userId));
    }
}
