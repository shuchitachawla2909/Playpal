package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.EventParticipantDto;

import java.util.List;

public interface EventParticipantService {
    EventParticipantDto joinEvent(Long eventId, Long userId);
    List<EventParticipantDto> listByEvent(Long eventId);
}
