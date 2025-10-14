package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateEventRequest;
import com.example.MyPlayPal.dto.EventDto;

import java.util.List;

public interface EventService {
    EventDto createEvent(CreateEventRequest req);
    EventDto getById(Long id);
    List<EventDto> listBySport(Long sportId);
}
