package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.UserSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserSportRepository extends JpaRepository<UserSport, Long> {

    List<UserSport> findByUserId(Long userId);

    boolean existsByUserAndSport(com.example.MyPlayPal.model.User user,
                                 com.example.MyPlayPal.model.Sport sport);

    // ✅ Add this missing method
    Optional<UserSport> findByUserIdAndSportId(Long userId, Long sportId);

    @Query("SELECT us FROM UserSport us " +
            "WHERE us.user.id != :currentUserId " +
            "AND us.user.id NOT IN (" +
            "   SELECT f.friend.id FROM Friend f WHERE f.user.id = :currentUserId" +
            ")")
    List<UserSport> findAllExceptCurrentUser(@Param("currentUserId") Long currentUserId);

    boolean existsByUserIdAndSportId(Long userId, Long sportId);

}
