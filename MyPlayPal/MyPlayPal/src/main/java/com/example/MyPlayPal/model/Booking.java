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

    // The service layer sets this field *after* the entity is created and saved.
    // By default, @Data gives a setter, but being explicit with @Setter is safer
    // if you use @Data with @Builder, as the built object is then modified.
    @Setter // ⭐ ADDED @Setter for transactional updates
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PaymentTransaction payment;

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}