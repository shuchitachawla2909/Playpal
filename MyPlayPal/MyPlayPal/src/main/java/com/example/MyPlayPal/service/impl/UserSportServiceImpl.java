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

    private final UserSportRepository repo;
    private final UserRepository userRepo;
    private final SportRepository sportRepo;

    public UserSportServiceImpl(UserSportRepository repo, UserRepository userRepo, SportRepository sportRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.sportRepo = sportRepo;
    }

    @Override
    @Transactional
    public UserSportDto addUserSport(CreateUserSportRequest req) {
        User u = userRepo.findById(req.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Sport s = sportRepo.findById(req.getSportId()).orElseThrow(() -> new ResourceNotFoundException("Sport not found"));
        // if exists update skill else create
        var existing = repo.findByUserIdAndSportId(u.getId(), s.getId());
        UserSport us;
        if (existing.isPresent()) {
            us = existing.get();
            us.setSkillLevel(req.getSkillLevel());
        } else {
            us = UserSport.builder().user(u).sport(s).skillLevel(req.getSkillLevel()).build();
        }
        UserSport saved = repo.save(us);
        return UserSportDto.builder().id(saved.getId()).userId(u.getId()).sportId(s.getId()).skillLevel(saved.getSkillLevel()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSportDto> listByUser(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(us -> UserSportDto.builder().id(us.getId()).userId(us.getUser().getId()).sportId(us.getSport().getId()).skillLevel(us.getSkillLevel()).build())
                .collect(Collectors.toList());
    }
}
