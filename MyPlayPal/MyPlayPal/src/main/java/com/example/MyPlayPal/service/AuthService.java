package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.AuthResponse;
import com.example.MyPlayPal.dto.LoginRequest;
import com.example.MyPlayPal.dto.UserSignupRequest;

public interface AuthService {
    AuthResponse signup(UserSignupRequest req);
    AuthResponse login(LoginRequest req);
}
