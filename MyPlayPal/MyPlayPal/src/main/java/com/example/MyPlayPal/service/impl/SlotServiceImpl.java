package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.SlotTemplateRequest;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.model.CourtSlot;
import com.example.MyPlayPal.repository.CourtRepository;
import com.example.MyPlayPal.repository.CourtSlotRepository;
import com.example.MyPlayPal.service.SlotService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class SlotServiceImpl implements SlotService {

    private final CourtRepository courtRepo;
    private final CourtSlotRepository slotRepo;

    public SlotServiceImpl(CourtRepository courtRepo, CourtSlotRepository slotRepo) {
        this.courtRepo = courtRepo;
        this.slotRepo = slotRepo;
    }

    @Override
    @Transactional
    public void generateSlotsForCourt(SlotTemplateRequest request) {
        Court court = courtRepo.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court not found: " + request.getCourtId()));

        for (LocalDate date = request.getStartDate();
             !date.isAfter(request.getEndDate());
             date = date.plusDays(1)) {

            LocalTime time = request.getStartTime();

            while (time.isBefore(request.getEndTime())) {
                LocalDateTime start = date.atTime(time);
                LocalDateTime end = start.plusMinutes(request.getSlotDurationMinutes());
                if (end.isAfter(date.atTime(request.getEndTime()))) break;

                boolean exists = slotRepo.findByCourtIdAndStartTimeAndEndTime(court.getId(), start, end).isPresent();
                if (!exists) {
                    CourtSlot slot = new CourtSlot();
                    slot.setCourt(court);
                    slot.setStartTime(start);
                    slot.setEndTime(end);
                    slot.setStatus(CourtSlot.SlotStatus.AVAILABLE);
                    slotRepo.save(slot);
                }
                time = time.plusMinutes(request.getSlotDurationMinutes());
            }
        }
    }
}
