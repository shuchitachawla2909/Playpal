package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "courts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String courtName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    private BigDecimal hourlyRate = BigDecimal.ZERO;
    private Boolean isBookable = true;
}
