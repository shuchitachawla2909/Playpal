package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateSportRequest;
import com.example.MyPlayPal.dto.SportDto;
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

    @GetMapping
    public ResponseEntity<List<SportDto>> listSports() {
        return ResponseEntity.ok(sportService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportDto> getSport(@PathVariable Long id) {
        return ResponseEntity.ok(sportService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SportDto> createSport(@Valid @RequestBody CreateSportRequest request) {
        return ResponseEntity.ok(sportService.createSport(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSport(@PathVariable Long id) {
        // implement if service supports deletion
        return ResponseEntity.ok("Delete sport endpoint not implemented");
    }
}
