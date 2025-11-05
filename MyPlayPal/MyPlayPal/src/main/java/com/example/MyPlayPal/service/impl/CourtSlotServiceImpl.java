package com.example.MyPlayPal.service.impl;

import com.example.MyPlayPal.dto.CreateCourtSlotRequest;
import com.example.MyPlayPal.dto.CourtSlotDto;
import com.example.MyPlayPal.dto.VenueSlotResponse;
import com.example.MyPlayPal.exception.ResourceNotFoundException;
import com.example.MyPlayPal.model.Court;
import com.example.MyPlayPal.model.CourtSlot;
import com.example.MyPlayPal.repository.CourtRepository;
import com.example.MyPlayPal.repository.CourtSlotRepository;
import com.example.MyPlayPal.service.CourtSlotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;

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

        // ✅ Check if slot already exists
        boolean exists = slotRepository.existsByCourtIdAndStartTimeAndEndTime(
                req.getCourtId(), req.getStartTime(), req.getEndTime());

        if (exists) {
            throw new IllegalStateException("Slot already exists for this court and time.");
        }

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
    public List<CourtSlotDto> findSlotsByCourtAndRange(Long courtId, LocalDateTime from, LocalDateTime to) {
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

    @Override
    @Transactional(readOnly = true)
    public List<VenueSlotResponse> getAvailableSlotsByVenue(Long venueId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureLimit = now.plusDays(30);

        List<CourtSlot> slots = slotRepository.findAvailableByVenueAndDateRange(venueId, now, futureLimit);

        Map<String, Map<String, List<CourtSlot>>> groupedSlots = slots.stream()
                .collect(Collectors.groupingBy(
                        slot -> slot.getCourt().getCourtname(),
                        Collectors.groupingBy(slot -> slot.getStartTime().toLocalDate().toString())
                ));

        List<VenueSlotResponse> response = new ArrayList<>();

        groupedSlots.forEach((courtName, dateMap) -> {
            dateMap.forEach((date, courtSlots) -> {
                Long courtId = courtSlots.stream().findFirst()
                        .map(s -> s.getCourt().getId())
                        .orElse(null);

                List<VenueSlotResponse.SlotInfo> slotInfos = courtSlots.stream()
                        .sorted(Comparator.comparing(CourtSlot::getStartTime))
                        .map(slot -> VenueSlotResponse.SlotInfo.builder()
                                .id(slot.getId())
                                .startTime(slot.getStartTime().toString())
                                .endTime(slot.getEndTime().toString())
                                .status(slot.getStatus().toString())
                                .build())
                        .collect(Collectors.toList());

                response.add(VenueSlotResponse.builder()
                        .courtName(courtName)
                        .courtId(courtId)
                        .date(date)
                        .slots(slotInfos)
                        .build());
            });
        });

        response.sort(Comparator.comparing(VenueSlotResponse::getDate)
                .thenComparing(VenueSlotResponse::getCourtName));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtSlotDto> getAvailableSlotsForCourtAndDate(Long courtId, LocalDate date) {
        // Get slots from your repository
        List<CourtSlot> slots = slotRepository.findAvailableSlotsByCourtAndDate(courtId, date);

        // Convert to DTOs
        return slots.stream()
                .map(slot -> CourtSlotDto.builder()
                        .id(slot.getId())
                        .courtId(slot.getCourt().getId())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .status(slot.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<CourtSlot> getSlotsByCourtId(Long courtId) {
        return slotRepository.findByCourtIdOrderByStartTimeAsc(courtId);
    }

    @Override
    @Transactional
    public void deleteSlotById(Long slotId) {
        slotRepository.deleteById(slotId);
    }


}
