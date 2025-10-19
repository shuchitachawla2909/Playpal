package com.example.MyPlayPal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String role;      // USER or MANAGER
    private String username;  // convenience for frontend

}
