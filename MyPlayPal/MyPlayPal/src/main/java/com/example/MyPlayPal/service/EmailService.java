package com.example.MyPlayPal.service;

public interface EmailService {
    void sendAdminNotification(String name, String email, String subject, String message);
}
