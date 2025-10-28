package com.example.MyPlayPal.mapper;

import com.example.MyPlayPal.dto.EventResponse;
import com.example.MyPlayPal.model.Event;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventMapper {

    public EventResponse toEventResponse(Event event) {
        if (event == null) return null;

        return EventResponse.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .organizerId(event.getOrganizer() != null ? event.getOrganizer().getId() : null)
                .organizerName(event.getOrganizer() != null ? event.getOrganizer().getUsername() : null)
                .slotIds(event.getSlots() != null
                        ? event.getSlots().stream()
                        .map(slot -> slot.getId())
                        .collect(Collectors.toList())
                        : null)
                .bookingDate(event.getBookingDate())
                .maxPlayers(event.getMaxPlayers())
                .currentPlayers(event.getCurrentPlayers())
                .description(event.getDescription())
                .skillLevelRequired(event.getSkillLevelRequired())
                .entryFee(event.getEntryFee())
                .status(event.getStatus() != null ? event.getStatus().name() : null)
                .totalAmount(event.getTotalAmount())
                .participantIds(event.getParticipants() != null
                        ? event.getParticipants().stream()
                        .map(p -> {
                            if (p.getUser() != null) {
                                return p.getUser().getId();
                            } else {
                                return null;
                            }
                        })
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}
