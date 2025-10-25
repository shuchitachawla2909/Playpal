package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name="events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="organizer_user_id")
    private User organizer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_slots",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "slot_id")
    )
    private List<CourtSlot> slots;


    private Instant bookingDate = Instant.now();

    private Integer maxPlayers;
    private Integer currentPlayers = 0;

    private String description;
    private String skillLevelRequired;
    private BigDecimal entryFee;

    // --- Organizer payment ---
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.PENDING;

    private BigDecimal totalAmount;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentTransaction payment;

    public enum EventStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    // Optional bidirectional mapping
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventParticipant> participants;
}
