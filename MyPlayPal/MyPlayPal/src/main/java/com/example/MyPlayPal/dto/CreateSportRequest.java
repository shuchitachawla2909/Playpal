package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSportRequest {
    @NotBlank(message = "sportName is required")
    @Size(max = 100)
    private String sportName;
    private String sportImageUrl;
}