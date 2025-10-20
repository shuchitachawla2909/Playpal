package com.example.MyPlayPal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "managers", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "contact")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manager {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contact;
    private String email;
    private String password;
}
