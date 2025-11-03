package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "friend")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // the one who sent the invite (A)

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend; // the one who was added (B)
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean seen = true;
}
