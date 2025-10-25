package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    // --- Find participants by event ---
    List<EventParticipant> findByEvent(Event event);

    // --- Find participants by user ---
    List<EventParticipant> findByUser(User user);

    // --- Check if a user is already part of an event ---
    Optional<EventParticipant> findByEventAndUser(Event event, User user);

    // --- Count participants by event and status ---
    long countByEventAndStatus(Event event, EventParticipant.ParticipantStatus status);


    @Query("SELECT ep.event FROM EventParticipant ep WHERE ep.user = :user")
    List<Event> findEventsJoinedByUser(@Param("user") User user);

}
