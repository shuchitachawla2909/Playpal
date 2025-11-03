package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateUserSportRequest;
import com.example.MyPlayPal.dto.UserSportDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Sport;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.UserSport;
import com.example.MyPlayPal.repository.SportRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.repository.UserSportRepository;
import com.example.MyPlayPal.service.UserSportService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserSportServiceImpl implements UserSportService {

    private final UserSportRepository userSportRepository;
    private final UserRepository userRepository;
    private final SportRepository sportRepository;

    public UserSportServiceImpl(UserSportRepository userSportRepository,
                                UserRepository userRepository,
                                SportRepository sportRepository) {
        this.userSportRepository = userSportRepository;
        this.userRepository = userRepository;
        this.sportRepository = sportRepository;
    }

    // ✅ Fetch all players except current user (with eager load to avoid LazyInitializationException)
    @Override
    @Transactional(readOnly = true)
    public List<UserSport> getAllOtherPlayers(Long currentUserId) {
        List<UserSport> players = userSportRepository.findAllExceptCurrentUser(currentUserId);

        // Ensure user and sport details are initialized
        players.forEach(us -> {
            us.getUser().getUsername();
            us.getUser().getContact();
            us.getSport().getSportname();
        });

        return players;
    }

    // ✅ Add or update user sport
    @Override
    public UserSportDto addUserSport(CreateUserSportRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Sport sport = sportRepository.findById(req.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found"));

        var existing = userSportRepository.findByUserIdAndSportId(user.getId(), sport.getId());
        UserSport userSport = existing.orElseGet(() -> new UserSport(null, user, sport, req.getSkillLevel()));

        userSport.setSkillLevel(req.getSkillLevel());
        UserSport saved = userSportRepository.save(userSport);

        return UserSportDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .sportId(sport.getId())
                .skillLevel(saved.getSkillLevel())
                .build();
    }

    // ✅ List all sports associated with a user
    @Override
    @Transactional(readOnly = true)
    public List<UserSportDto> listByUser(Long userId) {
        return userSportRepository.findByUserId(userId).stream()
                .map(us -> UserSportDto.builder()
                        .id(us.getId())
                        .userId(us.getUser().getId())
                        .sportId(us.getSport().getId())
                        .skillLevel(us.getSkillLevel())
                        .build())
                .collect(Collectors.toList());
    }

    // ✅ Link a user to a sport with random skill level (if not already linked)
    @Override
    public void linkUserToSport(Long userId, Long sportId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found"));

        var existing = userSportRepository.findByUserIdAndSportId(userId, sportId);
        if (existing.isPresent()) return;

        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        String randomSkillLevel = levels[new Random().nextInt(levels.length)];

        UserSport userSport = UserSport.builder()
                .user(user)
                .sport(sport)
                .skillLevel(randomSkillLevel)
                .build();

        userSportRepository.save(userSport);
    }

    // ✅ Add user sport for currently logged-in user
    @Override
    public void addUserSportForCurrentUser(Long sportId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Sport sport = sportRepository.findById(sportId)
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        boolean exists = userSportRepository.existsByUserAndSport(user, sport);
        if (!exists) {
            UserSport userSport = UserSport.builder()
                    .user(user)
                    .sport(sport)
                    .skillLevel("Any") // default value
                    .build();
            userSportRepository.save(userSport);
        }
    }
}
