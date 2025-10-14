package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateUserRequest;
import com.example.MyPlayPal.dto.UpdateUserRequest;
import com.example.MyPlayPal.dto.UserDto;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface UserService {
    List<UserDto> listAllUsers();
    UserDto getUserById(Long id);
    UserDto createUser(CreateUserRequest request);
    UserDto updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}


