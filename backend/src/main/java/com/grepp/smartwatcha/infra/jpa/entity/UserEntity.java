package com.grepp.smartwatcha.infra.jpa.entity;

import com.grepp.smartwatcha.infra.jpa.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean activated = true;

    @Enumerated(EnumType.STRING)
    private Role role;
}