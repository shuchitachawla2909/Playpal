package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByEmail(String email);
    Optional<Manager> findByContact(String contact);
}
