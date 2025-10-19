package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.AuthResponse;
import com.example.MyPlayPal.dto.LoginRequest;
import com.example.MyPlayPal.dto.UserSignupRequest;
import com.example.MyPlayPal.dto.ManagerSignupRequest;

public interface AuthService {

    // Sign up for user
    AuthResponse signupUser(UserSignupRequest req);

    // Sign up for manager
    AuthResponse signupManager(ManagerSignupRequest req);

    // Login checks both user and manager tables
    AuthResponse login(LoginRequest req);
}
