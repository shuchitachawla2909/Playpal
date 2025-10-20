package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.AuthResponse;
import com.example.MyPlayPal.dto.LoginRequest;
import com.example.MyPlayPal.dto.ManagerSignupRequest;
import com.example.MyPlayPal.dto.UserSignupRequest;
import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.ManagerRepository;
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
    private final ManagerRepository managerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ---------------------- USER SIGNUP ----------------------
    @Override
    public AuthResponse signupUser(UserSignupRequest req) {
        userRepo.findByUsername(req.getUsername())
                .ifPresent(u -> { throw new RuntimeException("Username already exists"); });
        userRepo.findByEmail(req.getEmail())
                .ifPresent(u -> { throw new RuntimeException("Email already exists"); });

        User user = User.builder()
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

        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, "USER", user.getUsername());
    }

    // ---------------------- MANAGER SIGNUP ----------------------
    @Override
    public AuthResponse signupManager(ManagerSignupRequest req) {
        managerRepo.findByEmail(req.getEmail())
                .ifPresent(m -> { throw new RuntimeException("Email already exists"); });

        Manager manager = Manager.builder()
                .managername(req.getManagername())
                .contact(req.getContact())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        managerRepo.save(manager);

        String token = jwtUtil.generateToken(manager.getManagername());
        return new AuthResponse(token, "MANAGER", manager.getManagername());
    }

    // ---------------------- LOGIN (checks both tables) ----------------------
    @Override
    public AuthResponse login(LoginRequest req) {
        // Try user table first
        var userOpt = userRepo.findByUsername(req.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            return new AuthResponse(
                    jwtUtil.generateToken(user.getUsername()),
                    "USER",
                    user.getUsername()
            );
        }

        // If not found, try manager table
        var managerOpt = managerRepo.findByManagername(req.getUsername());
        if (managerOpt.isPresent()) {
            Manager manager = managerOpt.get();
            if (!passwordEncoder.matches(req.getPassword(), manager.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            return new AuthResponse(
                    jwtUtil.generateToken(manager.getManagername()),
                    "MANAGER",
                    manager.getManagername()
            );
        }

        throw new RuntimeException("User/Manager not found");
    }
}

