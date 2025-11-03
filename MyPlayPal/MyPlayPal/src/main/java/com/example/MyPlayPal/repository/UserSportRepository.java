package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.Sport;
import com.example.MyPlayPal.model.UserSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSportRepository extends JpaRepository<UserSport, Long> {

    // ✅ Checks if a User-Sport pair already exists
    boolean existsByUserAndSport(User user, Sport sport);

    // ✅ Find all sports for a specific user
    List<UserSport> findByUserId(Long userId);

    // ✅ Find all users linked to a specific sport
    List<UserSport> findBySportId(Long sportId);

    // ✅ Find specific user-sport combination
    Optional<UserSport> findByUserIdAndSportId(Long userId, Long sportId);

    // ✅ Fetch all users except current user (with sport + user loaded eagerly)
    @Query("SELECT us FROM UserSport us " +
            "JOIN FETCH us.user u " +
            "JOIN FETCH us.sport s " +
            "WHERE u.id <> :currentUserId")
    List<UserSport> findAllExceptCurrentUser(Long currentUserId);
}
