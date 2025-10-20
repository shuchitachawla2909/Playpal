package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.*;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.repository.ManagerRepository;
import com.example.MyPlayPal.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepo;
    private final ManagerRepository managerRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ========================== USER SIGNUP ==========================
    @PostMapping("/signup/user")
    public ResponseEntity<?> registerUser(@Valid @ModelAttribute UserSignupRequest request) {
        log.info("User signup request: username={}, email={}", request.getUsername(), request.getEmail());

        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists for user");
        }
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists for user");
        }

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

        userRepo.save(user);
        log.info("New user registered: {}", user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, "USER", user.getUsername()));
    }

    // ========================== MANAGER SIGNUP ==========================
    @PostMapping("/signup/manager")
    public ResponseEntity<?> registerManager(@Valid @ModelAttribute ManagerSignupRequest request) {
        log.info("Manager signup request: name={}, email={}", request.getName(), request.getEmail());

        if (managerRepo.findByName(request.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Manager name already exists");
        }
        if (managerRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists for manager");
        }

        Manager manager = Manager.builder()
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .contact(request.getContact())
                .build();

        managerRepo.save(manager);
        log.info("New manager registered: {}", manager.getName());

        String token = jwtUtil.generateToken(manager.getName());
        return ResponseEntity.ok(new AuthResponse(token, "MANAGER", manager.getName()));
    }

    // ========================== LOGIN (common) ==========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @ModelAttribute LoginRequest request) {
        log.info("Login attempt for '{}'", request.getUsername());

        // ---- Try user first ----
        Optional<User> userOpt = userRepo.findByUsername(request.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername());
                return ResponseEntity.ok(new AuthResponse(token, "USER", user.getUsername()));
            }
            return ResponseEntity.status(401).body("Invalid credentials for user");
        }

        // ---- Try manager next ----
        Optional<Manager> managerOpt = managerRepo.findByName(request.getUsername());
        if (managerOpt.isPresent()) {
            Manager manager = managerOpt.get();
            if (passwordEncoder.matches(request.getPassword(), manager.getPassword())) {
                String token = jwtUtil.generateToken(manager.getName());
                return ResponseEntity.ok(new AuthResponse(token, "MANAGER", manager.getName()));
            }
            return ResponseEntity.status(401).body("Invalid credentials for manager");
        }

        // ---- Not found anywhere ----
        return ResponseEntity.status(404).body("User/Manager not found");
    }
}

