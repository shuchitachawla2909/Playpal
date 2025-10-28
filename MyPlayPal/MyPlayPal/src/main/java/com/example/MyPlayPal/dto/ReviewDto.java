package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Long venueId;
    private Long userId;


    private String userName;

    private Integer rating;
    private String comment;
    private Instant createdAt;
}