package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.EventParticipantDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Event;
import com.example.MyPlayPal.model.EventParticipant;
import com.example.MyPlayPal.model.User;
import com.example.MyPlayPal.model.UserSport;
import com.example.MyPlayPal.repository.EventParticipantRepository;
import com.example.MyPlayPal.repository.EventRepository;
import com.example.MyPlayPal.repository.UserRepository;
import com.example.MyPlayPal.repository.UserSportRepository;
import com.example.MyPlayPal.service.EventParticipantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventParticipantRepository repo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final UserSportRepository userSportRepo; // Added to check skills

    public EventParticipantServiceImpl(EventParticipantRepository repo,
                                       EventRepository eventRepo,
                                       UserRepository userRepo,
                                       UserSportRepository userSportRepo) {
        this.repo = repo;
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.userSportRepo = userSportRepo;
    }

    @Override
    @Transactional
    public EventParticipantDto joinEvent(Long eventId, Long userId) {
        Event e = eventRepo.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1️⃣ Check duplicate participation
        repo.findByEventIdAndUserId(eventId, userId).ifPresent(ep -> {
            throw new IllegalArgumentException("User already joined event");
        });

        // 2️⃣ Check skill level
        UserSport userSkill = userSportRepo.findByUserIdAndSportId(userId, e.getSport().getId())
                .orElseThrow(() -> new IllegalArgumentException("User has not declared skill for this sport"));

        if (!isSkillSufficient(userSkill.getSkillLevel(), e.getSkillLevelRequired())) {
            throw new IllegalArgumentException("User skill level does not meet event requirement");
        }

        // 3️⃣ Check event capacity
        if (e.getCurrentPlayers() >= e.getMaxPlayers()) {
            throw new IllegalStateException("Event is full");
        }

        // 4️⃣ Create participant record
        EventParticipant ep = EventParticipant.builder()
                .event(e)
                .user(u)
                .joinDate(Instant.now())
                .status("JOINED")
                .build();

        EventParticipant saved = repo.save(ep);

        // 5️⃣ Increment event's current player count
        e.setCurrentPlayers(e.getCurrentPlayers() + 1);
        eventRepo.save(e);

        return EventParticipantDto.builder()
                .id(saved.getId())
                .eventId(e.getId())
                .userId(u.getId())
                .joinDate(saved.getJoinDate())
                .status(saved.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventParticipantDto> listByEvent(Long eventId) {
        return repo.findByEventId(eventId).stream().map(ep -> EventParticipantDto.builder()
                .id(ep.getId())
                .eventId(ep.getEvent().getId())
                .userId(ep.getUser().getId())
                .joinDate(ep.getJoinDate())
                .status(ep.getStatus())
                .build()).collect(Collectors.toList());
    }

    // ✅ Helper: skill comparison
    private boolean isSkillSufficient(String userSkill, String requiredSkill) {
        List<String> levels = List.of("beginner", "intermediate", "advanced");
        int userIndex = levels.indexOf(userSkill.toLowerCase());
        int requiredIndex = levels.indexOf(requiredSkill.toLowerCase());
        return userIndex >= requiredIndex;
    }
}

