package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List; // ⭐ FIX: Import List here

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueDto {
    private Long id;
    private String venueName;
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private Long managerId;
    private Double rating;
    private String venueImageUrl;

    // Example fields added for booking flow:
    private String managerName;
    private String managerContact;

    // ⭐ CRITICAL FIELD for venue-detail.html
    private List<CourtDto> courts;
}