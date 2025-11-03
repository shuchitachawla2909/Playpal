package com.example.MyPlayPal.repository;

import com.example.MyPlayPal.model.Friend;
import com.example.MyPlayPal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findByUser(User user);     // Friends I added
    List<Friend> findByFriend(User friend); // People who added me
    Optional<Friend> findByUserAndFriend(User user, User friend);
    long countByFriendAndSeenFalse(User friend);
}
