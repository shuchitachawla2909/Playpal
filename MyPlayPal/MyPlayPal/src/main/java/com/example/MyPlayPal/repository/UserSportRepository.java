package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.UserSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSportRepository extends JpaRepository<UserSport, Long> {
    List<UserSport> findByUserId(Long userId);
    List<UserSport> findBySportId(Long sportId);
    Optional<UserSport> findByUserIdAndSportId(Long userId, Long sportId);
}
