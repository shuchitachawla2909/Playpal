package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.ContactMessageDto;
import com.example.MyPlayPal.dto.CreateContactMessageRequest;
import com.example.MyPlayPal.model.ContactMessage;
import com.example.MyPlayPal.repository.ContactMessageRepository;
import com.example.MyPlayPal.service.ContactMessageService;
import com.example.MyPlayPal.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactMessageServiceImpl implements ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailService emailService; // ✅ Inject EmailService

    @Override
    public ContactMessageDto createMessage(CreateContactMessageRequest request) {
        ContactMessage message = ContactMessage.builder()
                .name(request.getName())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .createdAt(Instant.now())
                .build();

        ContactMessage saved = contactMessageRepository.save(message);

        // ✅ Send admin email notification
        emailService.sendAdminNotification(
                request.getName(),
                request.getEmail(),
                request.getSubject(),
                request.getMessage()
        );

        return mapToDto(saved);
    }

    @Override
    public ContactMessageDto getById(Long id) {
        return contactMessageRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
    }

    @Override
    public List<ContactMessageDto> getAllMessages() {
        return contactMessageRepository.findAll()
                .stream()
                .map(ContactMessageDto::fromEntity)
                .sorted(Comparator.comparing(ContactMessageDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ContactMessageDto> getMessagesByEmail(String email) {
        return contactMessageRepository.findByEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ✅ Utility Mapper
    private ContactMessageDto mapToDto(ContactMessage message) {
        return ContactMessageDto.builder()
                .id(message.getId())
                .name(message.getName())
                .email(message.getEmail())
                .subject(message.getSubject())
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
