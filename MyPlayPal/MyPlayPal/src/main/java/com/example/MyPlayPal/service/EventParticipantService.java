package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.EventParticipantDto;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;

import java.util.List;

public interface EventParticipantService {
    // User joins an event
    EventParticipant joinEvent(Long eventId, User user);

    // User leaves an event
    void leaveEvent(Long eventId, User user);

    // Get all participants for an event
    List<EventParticipant> getParticipantsByEvent(Event event);

    // Get all events a user has joined
    List<EventParticipant> getEventsByUser(User user);

    // Check if user already joined an event
    boolean isUserParticipant(Event event, User user);

    // Count participants by status
    long countParticipantsByStatus(Event event, EventParticipant.ParticipantStatus status);

    // Get all participants by event ID
    List<EventParticipant> getParticipantsByEventId(Long eventId);

}
