package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Find all events created by a specific organizer
    List<Event> findByOrganizer(User organizer);

    // Find events by status
    List<Event> findByStatus(Event.EventStatus status);

    // Search by name (case-insensitive)
    List<Event> findByEventNameContainingIgnoreCase(String name);

    // Get events where player slots are not full
    List<Event> findByCurrentPlayersLessThan(Integer maxPlayers);

    // ✅ Corrected custom query for confirmed and not-full events for a sport
    @Query("SELECT e FROM Event e WHERE e.sport.id = :sportId AND e.currentPlayers < e.maxPlayers AND e.status = :status")
    List<Event> findBySportIdAndCurrentPlayersLessThanMaxPlayersAndStatus(
            @Param("sportId") Long sportId,
            @Param("status") Event.EventStatus status
    );

    // Find events by venue
    List<Event> findByVenue(Venue venue);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.venue WHERE e.id = :eventId")
    Optional<Event> findByIdWithVenue(@Param("eventId") Long eventId);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.venue " +
            "LEFT JOIN FETCH e.organizer " +
            "LEFT JOIN FETCH e.sport " +
            "WHERE e.id = :eventId")
    Optional<Event> findByIdWithDetails(@Param("eventId") Long eventId);


}
