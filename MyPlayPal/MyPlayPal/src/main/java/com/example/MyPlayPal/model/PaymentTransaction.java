package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    // Either a booking, an event (organizer), or a participant (entry fee)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="booking_id")
    private Booking booking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id")
    private Event event;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id")
    private EventParticipant participant;

    private BigDecimal amount;
    private Instant timestamp = Instant.now();

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.INITIATED;

    private String referenceId;

    public enum PaymentStatus {
        INITIATED, SUCCESS, FAILED, REFUNDED
    }
}
