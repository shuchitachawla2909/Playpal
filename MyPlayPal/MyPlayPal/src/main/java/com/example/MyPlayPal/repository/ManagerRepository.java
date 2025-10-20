package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByManagername(String managername);
    Optional<Manager> findByEmail(String email);
}
