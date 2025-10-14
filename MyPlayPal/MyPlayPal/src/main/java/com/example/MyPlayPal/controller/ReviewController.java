package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.ReviewDto;
import com.example.MyPlayPal.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> addReview(@Valid @RequestBody ReviewDto dto) {
        return ResponseEntity.ok(reviewService.addReview(dto));
    }

    @GetMapping("/by-venue/{venueId}")
    public ResponseEntity<List<ReviewDto>> listByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(reviewService.listByVenue(venueId));
    }
}
