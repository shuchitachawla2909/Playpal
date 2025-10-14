package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user_sport", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","sport_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sport_id")
    private Sport sport;

    private String skillLevel;
}
