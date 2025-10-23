package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by sport ID
    //List<Event> findBySportId(Long sportId);

    // Find all events created by a specific organizer
    List<Event> findByOrganizer(User organizer);

    // Find events by status
    List<Event> findByStatus(Event.EventStatus status);

    // Search by name (case-insensitive)
    List<Event> findByEventNameContainingIgnoreCase(String name);

    // Get events where player slots are not full
    List<Event> findByCurrentPlayersLessThan(Integer maxPlayers);

    // Fetch all upcoming events
    //List<Event> findByStartTimeAfter(LocalDateTime dateTime);
}
