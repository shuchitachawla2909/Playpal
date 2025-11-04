package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.ContactMessageDto;
import com.example.MyPlayPal.dto.CreateContactMessageRequest;

import java.util.List;

public interface ContactMessageService {

    // ✅ Create a new contact message from user input
    ContactMessageDto createMessage(CreateContactMessageRequest request);

    // ✅ Get a specific contact message by ID
    ContactMessageDto getById(Long id);

    // ✅ List all messages (for admin view)
    List<ContactMessageDto> getAllMessages();

    // ✅ List all messages sent by a particular user (based on email)
    List<ContactMessageDto> getMessagesByEmail(String email);

}
