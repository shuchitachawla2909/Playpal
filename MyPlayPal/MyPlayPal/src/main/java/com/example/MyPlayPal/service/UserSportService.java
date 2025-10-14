package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateUserSportRequest;
import com.example.MyPlayPal.dto.UserSportDto;

import java.util.List;

public interface UserSportService {
    UserSportDto addUserSport(CreateUserSportRequest req);
    List<UserSportDto> listByUser(Long userId);
}
