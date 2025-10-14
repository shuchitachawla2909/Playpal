package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(ReviewDto dto);
    List<ReviewDto> listByVenue(Long venueId);
}

