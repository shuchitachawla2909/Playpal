package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateCourtSlotRequest;
import com.example.MyPlayPal.dto.CourtSlotDto;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.model.CourtSlot;
import com.example.MyPlayPal.repository.CourtRepository;
import com.example.MyPlayPal.repository.CourtSlotRepository;
import com.example.MyPlayPal.service.CourtSlotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourtSlotServiceImpl implements CourtSlotService {

    private final CourtSlotRepository slotRepository;
    private final CourtRepository courtRepository;

    public CourtSlotServiceImpl(CourtSlotRepository slotRepository, CourtRepository courtRepository) {
        this.slotRepository = slotRepository;
        this.courtRepository = courtRepository;
    }

    @Override
    @Transactional
    public CourtSlotDto createSlot(CreateCourtSlotRequest req) {
        Court court = courtRepository.findById(req.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court not found"));
        CourtSlot slot = CourtSlot.builder()
                .court(court)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .status(CourtSlot.SlotStatus.AVAILABLE)
                .build();
        CourtSlot saved = slotRepository.save(slot);
        return CourtSlotDto.builder()
                .id(saved.getId())
                .courtId(court.getId())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .status(saved.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtSlotDto> findSlotsByCourtAndRange(Long courtId, java.time.LocalDateTime from, java.time.LocalDateTime to) {
        return slotRepository.findByCourtIdAndStartTimeBetween(courtId, from, to).stream()
                .map(s -> CourtSlotDto.builder()
                        .id(s.getId())
                        .courtId(s.getCourt().getId())
                        .startTime(s.getStartTime())
                        .endTime(s.getEndTime())
                        .status(s.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }
}
