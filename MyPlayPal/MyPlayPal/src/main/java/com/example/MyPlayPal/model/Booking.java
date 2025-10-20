package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="slot_id")
    private CourtSlot slot;

    private Instant bookingDate = Instant.now();

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    private BigDecimal totalAmount;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentTransaction payment;

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}
