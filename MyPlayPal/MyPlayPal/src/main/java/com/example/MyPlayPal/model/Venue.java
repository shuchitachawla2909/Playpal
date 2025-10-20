package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List; // Needed for the 'courts' collection

@Entity
@Table(name = "venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Assuming you have already corrected the database column names if necessary
    private String venueName;
    private String street;
    private String city;
    private String state;
    private String pinCode; // Corrected typo
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @Column(name = "venue_image_url")
    private String venueImageUrl;

    /**
     * ✅ FIX: Add the one-to-many relationship to the Court entity.
     * This resolves the "Cannot resolve method 'getCourts' in 'Venue'" error
     * because Lombok's @Data annotation will generate the getCourts() method.
     */
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Court> courts;
}