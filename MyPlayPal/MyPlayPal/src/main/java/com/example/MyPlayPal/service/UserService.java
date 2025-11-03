package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.UpdateUserRequest;
import com.example.MyPlayPal.dto.UserDto;
import com.example.MyPlayPal.dto.UserSignupRequest;
import com.example.MyPlayPal.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> listAllUsers();
    UserDto getUserById(Long id);
    UserDto createUser(UserSignupRequest request);
    UserDto updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    User getUserEntityById(Long id);

    Long getUserIdByUsername(String username);
}


