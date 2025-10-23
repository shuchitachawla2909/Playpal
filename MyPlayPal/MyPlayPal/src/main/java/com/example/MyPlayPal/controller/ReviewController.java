package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.CreateReviewRequest;
import com.example.MyPlayPal.dto.ReviewDto;
import com.example.MyPlayPal.model.User; // ADD THIS IMPORT
import com.example.MyPlayPal.repository.UserRepository; // ADD THIS IMPORT
import com.example.MyPlayPal.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository; // MOVE THIS TO FIELD LEVEL

    @PostMapping
    public ResponseEntity<ReviewDto> addReview(@Valid @RequestBody CreateReviewRequest request, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // FIX: Get the actual authenticated user details to extract user ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long secureUserId = null;

        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Get the username from Spring Security
            String username = authentication.getName();
            // You need to fetch the user ID from your user service using the username
            secureUserId = getUseridFromUsername(username);
        }

        if (secureUserId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ReviewDto createdReview = reviewService.addReview(request, secureUserId);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/by-venue/{venueId}")
    public ResponseEntity<List<ReviewDto>> listByVenue(@PathVariable Long venueId) {
        return ResponseEntity.ok(reviewService.listByVenue(venueId));
    }

    // Helper method to get user ID from username
    private Long getUseridFromUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}