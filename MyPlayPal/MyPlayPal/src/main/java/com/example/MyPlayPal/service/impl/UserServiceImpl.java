package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.UpdateUserRequest;
import com.example.MyPlayPal.dto.UserDto;
import com.example.MyPlayPal.dto.UserSignupRequest;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Map User entity to UserDto
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .contact(user.getContact())
                .city(user.getCity())
                .state(user.getState())
                .age(user.getAge())
                .gender(user.getGender())
                .profilePictureUrl(user.getProfilePictureUrl())
                .registrationDate(user.getRegistrationDate())
                .build();
    }

    @Override
    public List<UserDto> listAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToDto(user);
    }

    @Override
    public UserDto createUser(UserSignupRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                // encode the password here (exactly once)
                .password(passwordEncoder.encode(request.getPassword()))
                .contact(request.getContact())
                .city(request.getCity())
                .state(request.getState())
                .age(request.getAge())
                .gender(request.getGender())
                .profilePictureUrl(request.getProfilePictureUrl())
                .registrationDate(Instant.now())
                .build();

        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        // If client provided a new password, encode it before saving
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getContact() != null) user.setContact(request.getContact());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getProfilePictureUrl() != null) user.setProfilePictureUrl(request.getProfilePictureUrl());

        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }
}



