package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Manager;
import com.example.MyPlayPal.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByManager(Manager manager);
    List<Venue> findByCity(String city);
    List<Venue> findByManagerId(Long managerId);
}