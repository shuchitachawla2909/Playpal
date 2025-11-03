// com.example.MyPlayPal.service.impl.FriendServiceImpl.java
package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.model.Friend;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.repository.FriendRepository;
import com.example.MyPlayPal.service.FriendService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    public FriendServiceImpl(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @Override
    public void addFriend(User adder, User added) {
        if (friendRepository.findByUserAndFriend(adder, added).isEmpty()) {
            Friend friend = new Friend();
            friend.setUser(adder);
            friend.setFriend(added);
            friend.setSeen(false);
            friendRepository.save(friend);
        }
    }

    @Override
    public List<User> getMyFriends(User adder) {
        return friendRepository.findByUser(adder)
                .stream().map(Friend::getFriend).toList();
    }

    @Override
    public List<User> getAddedMeList(User added) {
        return friendRepository.findByFriend(added)
                .stream().map(Friend::getUser).toList();
    }

    @Override
    public long getUnseenCount(User added) {
        return friendRepository.countByFriendAndSeenFalse(added);
    }

    @Override
    public void markAllSeen(User added) {
        List<Friend> requests = friendRepository.findByFriend(added);
        for (Friend f : requests) {
            if (!f.isSeen()) {
                f.setSeen(true);
                friendRepository.save(f);
            }
        }
    }

    @Override
    public void removeFriend(User adder, User added) {
        friendRepository.findByUserAndFriend(adder, added)
                .ifPresent(friendRepository::delete);
    }
}
