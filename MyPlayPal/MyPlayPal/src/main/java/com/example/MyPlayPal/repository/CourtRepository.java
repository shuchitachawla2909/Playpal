package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourtRepository extends JpaRepository<Court, Long> {

    // find all courts for a given venue
    List<Court> findByVenueId(Long venueId);

    // find all courts for a given sport
    List<Court> findBySportId(Long sportId);

    @Query("SELECT DISTINCT c.venue FROM Court c WHERE c.sport.id = :sportId")
    List<Venue> findDistinctVenuesBySportId(Long sportId);

    // pageable version for UI lists
    Page<Court> findByVenueId(Long venueId, Pageable pageable);

    // find bookable courts in a city (if Court has venue.city relationship - use join in service or a custom query)
    List<Court> findByIsBookableTrueAndVenue_City(String city);
}

