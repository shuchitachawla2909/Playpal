package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(Long userId, String message);
    List<NotificationDto> listForUser(Long userId);
}
