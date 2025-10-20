package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateManagerRequest {
    @NotBlank(message = "name is required")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "contact is required")
    @Pattern(regexp = "\\d{7,15}", message = "contact must be digits (7-15 digits)")
    private String contact;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100)
    private String password;
}