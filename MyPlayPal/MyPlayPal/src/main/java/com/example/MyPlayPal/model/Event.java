package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="organizer_user_id")
    private User organizer;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sport_id")
    private Sport sport;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="venue_id")
    private Venue venue;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxPlayers;

    private Integer currentPlayers = 0;

    private String description;
    private String skillLevelRequired;
    private java.math.BigDecimal entryFee;
}
