package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVenueRequest {
    @NotBlank(message = "venueName is required")
    @Size(max = 200)
    private String venueName;

    @Size(max = 300)
    private String street;

    @NotBlank(message = "city is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "state is required")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "pinCode is required")
    @Size(max = 20)
    private String pinCode;

    @NotNull(message = "managerId is required")
    private Long managerId;

    private String venueImageUrl;
}
