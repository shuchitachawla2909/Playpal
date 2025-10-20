package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.UpdateUserRequest;
import com.example.MyPlayPal.dto.UserDto;
import com.example.MyPlayPal.dto.UserSignupRequest;

import java.util.List;

public interface UserService {
    List<UserDto> listAllUsers();
    UserDto getUserById(Long id);
    UserDto createUser(UserSignupRequest request);
    UserDto updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);

    // ⭐ FIX 3A: Added method signature for securely retrieving user ID
    Long getUserIdByUsername(String username);
}

