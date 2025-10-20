package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerSignupRequest {

    @NotBlank(message = "Manager name is required")
    private String managername;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Contact is required")
    private String contact;
}
