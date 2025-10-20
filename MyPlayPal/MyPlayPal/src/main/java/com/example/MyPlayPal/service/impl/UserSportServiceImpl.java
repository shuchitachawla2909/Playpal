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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserSportServiceImpl implements UserSportService {

    private final UserSportRepository userSportRepository;
    private final UserRepository userRepository;
    private final SportRepository sportRepository;

    // ✅ Constructor injection
    public UserSportServiceImpl(UserSportRepository userSportRepository,
                                UserRepository userRepository,
                                SportRepository sportRepository) {
        this.userSportRepository = userSportRepository;
        this.userRepository = userRepository;
        this.sportRepository = sportRepository;
    }

    // ✅ Fetch all players except current user
    @Override
    @Transactional(readOnly = true)
    public List<UserSport> getAllOtherPlayers(Long currentUserId) {
        return userSportRepository.findAllExceptCurrentUser(currentUserId);
    }

    // ✅ Add or update user sport entry
    @Override
    @Transactional
    public UserSportDto addUserSport(CreateUserSportRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Sport sport = sportRepository.findById(req.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found"));

        var existing = userSportRepository.findByUserIdAndSportId(user.getId(), sport.getId());
        UserSport userSport;

        if (existing.isPresent()) {
            userSport = existing.get();
            userSport.setSkillLevel(req.getSkillLevel());
        } else {
            userSport = UserSport.builder()
                    .user(user)
                    .sport(sport)
                    .skillLevel(req.getSkillLevel())
                    .build();
        }

        UserSport saved = userSportRepository.save(userSport);

        return UserSportDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .sportId(sport.getId())
                .skillLevel(saved.getSkillLevel())
                .build();
    }

    // ✅ List all sports associated with a particular user
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
}
