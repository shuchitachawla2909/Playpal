package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sportName;

    @Column(name = "sport_image_url")
    private String sportImageUrl;
}
