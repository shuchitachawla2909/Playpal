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
import com.example.MyPlayPal.service.ReviewService;
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
    // UPDATED: Accept CreateReviewRequest and secure userId
    public ReviewDto addReview(CreateReviewRequest request, Long userId) {
        // 1. Fetch related entities
        Venue v = venueRepo.findById(request.getVenueId()).orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + request.getVenueId()));
        // Use the SECURE userId to fetch the User entity
        User u = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // OPTIONAL: Check for existing review (Business logic to prevent multiple reviews)
        if (reviewRepo.existsByVenueIdAndUserId(request.getVenueId(), userId)) {
            throw new IllegalStateException("User " + userId + " has already reviewed Venue " + request.getVenueId());
        }

        // 2. Map DTO to Entity
        Review r = Review.builder()
                .venue(v)
                .user(u)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .build();

        // 3. Save Entity
        Review saved = reviewRepo.save(r);

        // 4. Map saved Entity back to DTO for response
        return ReviewDto.builder()
                .id(saved.getId())
                .venueId(saved.getVenue().getId())
                .userId(saved.getUser().getId())
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // Helper method to calculate average rating
    private Double calculateAverageRating(Long venueId) {
        // Use the new repository method
        return reviewRepo.findAverageRatingByVenueId(venueId)
                .orElse(0.0); // Return 0.0 if no reviews exist
    }
    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long venueId) { // <--- NEW METHOD IMPLEMENTATION
        // Uses the custom JpaRepository query
        return reviewRepo.findAverageRatingByVenueId(venueId)
                .orElse(0.0); // Return 0.0 if no reviews exist
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> listByVenue(Long venueId) {
        // This method fetches the list of individual reviews.
        return reviewRepo.findByVenueIdOrderByCreatedAtDesc(venueId).stream()
                .map(r -> ReviewDto.builder()
                        .id(r.getId())
                        .venueId(r.getVenue().getId())
                        // Note: Assuming User entity has a getName() method or similar for 'userName'
                        .userId(r.getUser() == null ? null : r.getUser().getId())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        // You'll likely need to update ReviewDto and the mapping to include userName
                        .build())
                .collect(Collectors.toList());
    }
}