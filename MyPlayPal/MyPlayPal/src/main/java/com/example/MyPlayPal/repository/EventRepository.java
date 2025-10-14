package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // events by sport
    List<Event> findBySportId(Long sportId);

    // upcoming events by sport
    List<Event> findBySportIdAndStartTimeAfter(Long sportId, Instant now);

    // pageable
    Page<Event> findBySportId(Long sportId, Pageable pageable);

    // events in time-range for a sport
    List<Event> findBySportIdAndStartTimeBetween(Long sportId, Instant from, Instant to);
}
