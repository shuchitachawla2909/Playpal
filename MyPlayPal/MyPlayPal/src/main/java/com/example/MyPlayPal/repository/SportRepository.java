package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    Optional<Sport> findBySportname(String sportname);
}
