package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateReviewRequest;
import com.example.MyPlayPal.dto.ReviewDto;

import java.util.List;

public interface ReviewService {

    ReviewDto addReview(CreateReviewRequest request, Long userId);

    List<ReviewDto> listByVenue(Long venueId);

    // NEW METHOD: Get the calculated average rating
    Double getAverageRating(Long venueId); // <--- ADD THIS
}