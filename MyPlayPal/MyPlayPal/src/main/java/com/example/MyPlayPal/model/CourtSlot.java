package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "court_slots", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"court_id", "start_time", "end_time"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.AVAILABLE;

    public enum SlotStatus {
        AVAILABLE, RESERVED, BOOKED, MAINTENANCE
    }

    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }
}
