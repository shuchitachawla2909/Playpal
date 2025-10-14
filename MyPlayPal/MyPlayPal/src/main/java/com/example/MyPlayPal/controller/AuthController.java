package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.AuthResponse;
import com.example.MyPlayPal.dto.LoginRequest;
import com.example.MyPlayPal.dto.UserSignupRequest;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequest request) {
        // 1) Log incoming request (for debugging)
        log.info("Signup request received: username='{}', email='{}', contact='{}'",
                request.getUsername(), request.getEmail(), request.getContact());

        // 2) Double-check request fields in runtime (helps find mapping issues)
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("email is required");
        }

        // 3) Check duplicates
        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // 4) Build and save User (explicitly set email)
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .contact(request.getContact())
                .city(request.getCity())
                .state(request.getState())
                .age(request.getAge())
                .gender(request.getGender())
                .registrationDate(Instant.now())
                .build();

        User saved = userRepo.save(user);
        log.info("Saved user id={} username={} email={}", saved.getId(), saved.getUsername(), saved.getEmail());

        String token = jwtUtil.generateToken(saved.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for username='{}'", request.getUsername());
        var opt = userRepo.findByUsername(request.getUsername());
        if (opt.isEmpty()) return ResponseEntity.status(401).body("User not found");

        User user = opt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
