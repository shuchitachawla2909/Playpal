package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserSportRequest {
    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "sportId is required")
    private Long sportId;

    @Pattern(regexp = "Beginner|Intermediate|Advanced|Any", message = "skillLevel must be Beginner/Intermediate/Advanced/Any")
    private String skillLevel;
}
