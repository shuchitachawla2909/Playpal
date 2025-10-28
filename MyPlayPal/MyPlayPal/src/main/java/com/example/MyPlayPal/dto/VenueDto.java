package com.example.MyPlayPal.dto;
import java.util.List;
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

    private Integer reviewCount;

    // Example fields added for booking flow:
    private String managerName;
    private String managerContact;

    // ⭐ CRITICAL FIELD for venue-detail.html
    private List<CourtDto> courts;

    private List<ReviewDto> reviews;

}
