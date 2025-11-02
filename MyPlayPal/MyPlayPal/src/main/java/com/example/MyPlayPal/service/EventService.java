package com.example.MyPlayPal.service;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.User;

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

    // Get events with available slots
    List<Event> getAvailableEvents();

    // ✅ Get confirmed events for a specific sport that still have vacancies
    List<Event> getAvailableConfirmedEventsBySport(Long sportId);

    // ✅ Get events by venue
    List<Event> getEventsByVenue(Long venueId);

    // Update event details
    Event updateEvent(Event event);

    // Cancel event
    void cancelEvent(Long eventId);
}
