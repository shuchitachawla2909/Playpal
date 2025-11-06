package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateUserSportRequest;
import com.example.MyPlayPal.dto.UserSportDto;
import com.example.MyPlayPal.model.UserSport;

import java.util.List;

public interface UserSportService {
    List<UserSportDto> listByUser(Long userId);
    List<UserSport> getAllOtherPlayers(Long currentUserId);
    void linkUserToSport(Long userId, Long sportId);
    void addUserSportForCurrentUser(Long sportId);

    void addUserSport(Long userId, Long sportId);
}
