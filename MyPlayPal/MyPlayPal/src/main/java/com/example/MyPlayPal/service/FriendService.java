package com.example.MyPlayPal.service;

import com.example.MyPlayPal.model.User;
import java.util.List;

public interface FriendService {
    void addFriend(User adder, User added);

    List<User> getMyFriends(User adder);

    List<User> getAddedMeList(User added);

    long getUnseenCount(User added);

    void markAllSeen(User added);

    void removeFriend(User adder, User added);
}
