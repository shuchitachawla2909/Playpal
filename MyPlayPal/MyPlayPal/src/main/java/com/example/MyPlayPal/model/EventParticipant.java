package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name="event_participants", uniqueConstraints = {@UniqueConstraint(columnNames = {"event_id","user_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    private Instant joinDate = Instant.now();

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status = ParticipantStatus.JOINED;

    public enum ParticipantStatus { JOINED, PENDING, CANCELLED }

    @OneToOne(mappedBy = "participant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private PaymentTransaction payment;

}
