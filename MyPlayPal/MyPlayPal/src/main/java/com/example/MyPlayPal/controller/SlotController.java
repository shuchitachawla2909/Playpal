package com.example.MyPlayPal.controller;

import com.example.MyPlayPal.dto.VenueSlotResponse;
import com.example.MyPlayPal.model.CourtSlot;
import com.example.MyPlayPal.repository.CourtSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final CourtSlotRepository courtSlotRepository;

    @GetMapping("/{venueId}")
    public List<VenueSlotResponse> getAvailableSlotsByVenue(@PathVariable Long venueId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureLimit = now.plusDays(30);

        List<CourtSlot> slots = courtSlotRepository.findByVenueAndDateRange(venueId, now, futureLimit);

        Map<String, Map<String, List<CourtSlot>>> groupedSlots = slots.stream()
                .collect(Collectors.groupingBy(
                        slot -> slot.getCourt().getCourtname(),
                        Collectors.groupingBy(
                                slot -> slot.getStartTime().toLocalDate().toString()
                        )
                ));

        List<VenueSlotResponse> response = new ArrayList<>();

        groupedSlots.forEach((courtName, dateMap) -> {
            dateMap.forEach((date, courtSlots) -> {
                Long courtId = courtSlots.isEmpty() ? null : courtSlots.get(0).getCourt().getId();

                List<VenueSlotResponse.SlotInfo> slotInfos = courtSlots.stream()
                        .sorted(Comparator.comparing(CourtSlot::getStartTime))
                        .map(slot -> VenueSlotResponse.SlotInfo.builder()
                                .id(slot.getId())
                                .startTime(slot.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .endTime(slot.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
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
}
