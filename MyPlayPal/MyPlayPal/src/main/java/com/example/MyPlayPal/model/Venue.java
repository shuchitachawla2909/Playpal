package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
}
