package com.example.MyPlayPal.dto;

import com.example.MyPlayPal.model.ContactMessage;
import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactMessageDto {
    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private Instant createdAt;

    public static ContactMessageDto fromEntity(com.example.MyPlayPal.model.ContactMessage contactMessage) {
        return ContactMessageDto.builder()
                .id(contactMessage.getId())
                .name(contactMessage.getName())
                .email(contactMessage.getEmail())
                .subject(contactMessage.getSubject())
                .message(contactMessage.getMessage())
                .createdAt(contactMessage.getCreatedAt())
                .build();
    }

    public static com.example.MyPlayPal.model.ContactMessage toEntity(ContactMessageDto dto) {
        return com.example.MyPlayPal.model.ContactMessage.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .build();
    }


}
