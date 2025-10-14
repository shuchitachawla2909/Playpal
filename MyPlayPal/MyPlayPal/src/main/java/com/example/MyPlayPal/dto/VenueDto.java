package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueDto {
    private Long id;
    private String venuename;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private Long managerId;
    private Double rating;
    private String venueImageUrl;
}
