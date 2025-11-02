package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String venuename;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @Column(name = "venue_image_url")
    private String venueImageUrl;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Court> courts;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events;

    // 🔹 Computed address field — not stored in DB
    @Transient
    public String getAddress() {
        return List.of(street, city, state, pincode).stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(", "));
    }

}
