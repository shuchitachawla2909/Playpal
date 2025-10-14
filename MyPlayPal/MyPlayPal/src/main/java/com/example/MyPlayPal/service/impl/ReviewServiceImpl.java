package com.example.MyPlayPal.service.impl;

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
    public ReviewDto addReview(ReviewDto dto) {
        Venue v = venueRepo.findById(dto.getVenueId()).orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        User u = userRepo.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Review r = Review.builder().venue(v).user(u).rating(dto.getRating()).comment(dto.getComment()).createdAt(Instant.now()).build();
        Review saved = reviewRepo.save(r);
        return ReviewDto.builder().id(saved.getId()).venueId(v.getId()).userId(u.getId()).rating(saved.getRating()).comment(saved.getComment()).createdAt(saved.getCreatedAt()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> listByVenue(Long venueId) {
        return reviewRepo.findByVenueIdOrderByCreatedAtDesc(venueId).stream()
                .map(r -> ReviewDto.builder().id(r.getId()).venueId(r.getVenue().getId()).userId(r.getUser() == null ? null : r.getUser().getId()).rating(r.getRating()).comment(r.getComment()).createdAt(r.getCreatedAt()).build())
                .collect(Collectors.toList());
    }
}

