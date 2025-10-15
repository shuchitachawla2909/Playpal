package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateReviewRequest;
import com.example.MyPlayPal.dto.ReviewDto;
import com.example.MyPlayPal.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Import Principal
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    // UPDATED: Inject Principal to securely get the User ID
    public ResponseEntity<ReviewDto> addReview(@Valid @RequestBody CreateReviewRequest request, Principal principal) {
        if (principal == null) {
            // Should be handled by Spring Security, but good for defense-in-depth
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // IMPORTANT: Assuming the Principal.getName() returns the UserID (as a String representation of a Long).
        // This is standard if you use a custom UserDetails service that maps your UserID to the Principal name.
        // Adjust this logic if your Principal stores the ID differently (e.g., in a custom object).
        Long secureUserId = Long.parseLong(principal.getName());

        // Pass the request DTO and the secure User ID to the service
        ReviewDto createdReview = reviewService.addReview(request, secureUserId);

        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/by-venue/{venueId}")
    public ResponseEntity<List<ReviewDto>> listByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(reviewService.listByVenue(venueId));
    }
}