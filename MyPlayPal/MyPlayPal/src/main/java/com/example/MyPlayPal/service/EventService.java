package com.example.MyPlayPal.service;

import com.example.MyPlayPal.dto.CreateEventRequest;
import com.example.MyPlayPal.dto.EventDto;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {

    // Create a new event
    Event createEvent(Event event);

    // Get event by ID
    Optional<Event> getEventById(Long eventId);

    // Get all events
    List<Event> getAllEvents();

    // Get events by organizer
    List<Event> getEventsByOrganizer(User organizer);

    // Get events by status
    List<Event> getEventsByStatus(Event.EventStatus status);

    // Search events by name
    List<Event> searchEventsByName(String name);

    // Get upcoming events
    //List<Event> getUpcomingEvents(LocalDateTime now);

    // Get events with available slots
    List<Event> getAvailableEvents();

    // Update event details
    Event updateEvent(Event event);

    // Cancel event
    void cancelEvent(Long eventId);
}
