package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.service.ReviewService;
import com.example.MyPlayPal.dto.CreateReviewRequest;
import com.example.MyPlayPal.dto.ReviewDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Review;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.Venue;
import com.example.MyPlayPal.repository.ReviewRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final VenueRepository venueRepo;
    private final UserRepository userRepo;

    public ReviewServiceImpl(ReviewRepository reviewRepo, VenueRepository venueRepo, UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.venueRepo = venueRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public ReviewDto addReview(CreateReviewRequest request, Long userId) {
        // 1. Fetch related entities
        Venue v = venueRepo.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + request.getVenueId()));

        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // OPTIONAL: Check for existing review (Business logic to prevent multiple reviews)
        if (reviewRepo.existsByVenueIdAndUserId(request.getVenueId(), userId)) {
            throw new IllegalStateException("You have already reviewed this venue");
        }

        if (request.getRating() < 0 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        // 2. Map DTO to Entity - CREATE A FRESH REVIEW WITHOUT MODIFYING USER
        Review r = Review.builder()
                .venue(v)
                .user(u)  // This attaches the user but shouldn't modify it
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .build();

        // 3. Save Entity
        Review saved = reviewRepo.save(r);

        // 4. Update venue rating
        updateVenueAverageRating(request.getVenueId());

        // 5. Map saved Entity back to DTO for response - DON'T TOUCH THE USER ENTITY
        return ReviewDto.builder()
                .id(saved.getId())
                .venueId(saved.getVenue().getId())
                .userId(saved.getUser().getId())
                .userName(saved.getUser().getUsername())  // Just read, don't modify
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long venueId) {
        return reviewRepo.findAverageRatingByVenueId(venueId)
                .orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> listByVenue(Long venueId) {
        return reviewRepo.findByVenueIdOrderByCreatedAtDesc(venueId).stream()
                .map(r -> ReviewDto.builder()
                        .id(r.getId())
                        .venueId(r.getVenue().getId())
                        .userId(r.getUser() == null ? null : r.getUser().getId())
                        .userName(r.getUser() == null ? "Anonymous" : r.getUser().getUsername())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // Helper method to update venue's average rating
    @Transactional
    private void updateVenueAverageRating(Long venueId) {
        Double avgRating = getAverageRating(venueId);
        Venue venue = venueRepo.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        // Round to 1 decimal place
        venue.setRating(Math.round(avgRating * 10.0) / 10.0);
        venueRepo.save(venue);
    }
}