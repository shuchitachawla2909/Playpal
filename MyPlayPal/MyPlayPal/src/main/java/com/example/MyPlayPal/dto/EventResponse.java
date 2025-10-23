package com.example.MyPlayPal.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String eventName;

    private Long organizerId;
    private String organizerName; // optional for display

    private List<Long> slotIds; // associated CourtSlot IDs

    private Instant bookingDate;

    private Integer maxPlayers;
    private Integer currentPlayers;

    private String description;
    private String skillLevelRequired;
    private BigDecimal entryFee;

    private String status; // PENDING, CONFIRMED, CANCELLED
    private BigDecimal totalAmount;

    private List<Long> participantIds; // IDs of participants
}
