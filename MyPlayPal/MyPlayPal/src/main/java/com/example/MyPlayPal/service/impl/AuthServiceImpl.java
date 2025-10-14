package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.AuthResponse;
import com.example.MyPlayPal.dto.UserSignupRequest;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.security.JwtUtil;
import com.example.MyPlayPal.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse signup(UserSignupRequest req) {
        // prevent duplicates
        userRepo.findByUsername(req.getUsername()).ifPresent(u -> { throw new RuntimeException("username exists"); });
        userRepo.findByEmail(req.getEmail()).ifPresent(u -> { throw new RuntimeException("email exists"); });

        User u = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .email(req.getEmail())
                .contact(req.getContact())
                .city(req.getCity())
                .state(req.getState())
                .age(req.getAge())
                .gender(req.getGender())
                .registrationDate(Instant.now())
                .build();

        userRepo.save(u);

        String token = jwtUtil.generateToken(u.getUsername());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(com.example.MyPlayPal.dto.LoginRequest req) {
        var userOpt = userRepo.findByUsername(req.getUsername());
        var user = userOpt.orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return new AuthResponse(jwtUtil.generateToken(user.getUsername()));
    }
}

