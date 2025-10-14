package com.example.MyPlayPal.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    // all fields optional for partial update — but validate format when present
    @Size(min = 3, max = 50, message = "username must be 3-50 characters")
    private String username;

    @Email(message = "email must be valid")
    private String email;

    @Size(min = 6, max = 100, message = "password must be at least 6 characters")
    private String password;

    @Pattern(regexp = "\\d{7,15}", message = "contact must be digits (7-15 digits)")
    private String contact;

    @Size(max = 100, message = "city max length is 100")
    private String city;

    @Size(max = 100, message = "state max length is 100")
    private String state;

    @Min(value = 1, message = "age must be positive")
    @Max(value = 150, message = "age seems invalid")
    private Integer age;

    @Pattern(regexp = "Male|Female|Other", message = "gender must be Male, Female or Other")
    private String gender;

    @Size(max = 500, message = "profilePictureUrl too long")
    private String profilePictureUrl;
}
