package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.NotificationDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Notification;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.NotificationRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;
    private final UserRepository userRepo;

    public NotificationServiceImpl(NotificationRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public NotificationDto createNotification(Long userId, String message) {
        User u = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Notification n = Notification.builder().user(u).message(message).isRead(false).build();
        Notification saved = repo.save(n);
        return NotificationDto.builder().id(saved.getId()).userId(u.getId()).message(saved.getMessage()).isRead(saved.getIsRead()).createdAt(saved.getCreatedAt()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> listForUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> NotificationDto.builder().id(n.getId()).userId(n.getUser().getId()).message(n.getMessage()).isRead(n.getIsRead()).createdAt(n.getCreatedAt()).build())
                .collect(Collectors.toList());
    }
}


