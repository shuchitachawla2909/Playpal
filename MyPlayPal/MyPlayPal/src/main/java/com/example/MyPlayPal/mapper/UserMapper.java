package com.example.MyPlayPal.mapper;

import com.example.MyPlayPal.dto.CreateUserRequest;
import com.example.MyPlayPal.dto.UpdateUserRequest;
import com.example.MyPlayPal.dto.UserDto;
import com.example.MyPlayPal.model.User;

public class UserMapper {

    public static UserDto toDto(User u) {
        if (u == null) return null;
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .contact(u.getContact())
                .city(u.getCity())
                .state(u.getState())
                .profilePictureUrl(u.getProfilePictureUrl())
                .age(u.getAge())
                .gender(u.getGender())
                .registrationDate(u.getRegistrationDate())
                .build();
    }

    public static User fromCreateRequest(CreateUserRequest req, String hashedPassword) {
        User u = new User();
        u.setUsername(req.getUsername());
        u.setEmail(req.getEmail());
        u.setPassword(hashedPassword);
        u.setContact(req.getContact());
        u.setCity(req.getCity());
        u.setState(req.getState());
        u.setProfilePictureUrl(req.getProfilePictureUrl());
        u.setAge(req.getAge());
        u.setGender(req.getGender());
        return u;
    }

    public static void applyUpdate(User target, UpdateUserRequest req) {
        if (req.getUsername() != null) target.setUsername(req.getUsername());
        if (req.getEmail() != null) target.setEmail(req.getEmail());
        if (req.getCity() != null) target.setCity(req.getCity());
        if (req.getState() != null) target.setState(req.getState());
        if (req.getProfilePictureUrl() != null) target.setProfilePictureUrl(req.getProfilePictureUrl());
        if (req.getAge() != null) target.setAge(req.getAge());
        if (req.getGender() != null) target.setGender(req.getGender());
    }
}

