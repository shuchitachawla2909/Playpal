package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.service.FriendService;
import com.example.MyPlayPal.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    public FriendController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    // ✅ Add friend
    @PostMapping("/add/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long friendId, Principal principal) {
        Long userId = userService.getUserIdByUsername(principal.getName());
        User adder = userService.getUserEntityById(userId);
        User added = userService.getUserEntityById(friendId);

        friendService.addFriend(adder, added);
        return ResponseEntity.ok("Friend added successfully!");
    }

    // ✅ Get my friends
    @GetMapping("/my-friends")
    public ResponseEntity<List<User>> getMyFriends(Principal principal) {
        Long userId = userService.getUserIdByUsername(principal.getName());
        User user = userService.getUserEntityById(userId);
        return ResponseEntity.ok(friendService.getMyFriends(user));
    }

    // ✅ Get who added me
    @GetMapping("/added-me")
    public ResponseEntity<List<User>> getAddedMe(Principal principal) {
        Long userId = userService.getUserIdByUsername(principal.getName());
        User user = userService.getUserEntityById(userId);
        return ResponseEntity.ok(friendService.getAddedMeList(user));
    }


}
