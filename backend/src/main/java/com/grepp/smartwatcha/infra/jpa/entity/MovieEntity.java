package com.grepp.smartwatcha.infra.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String posterUrl;
    private String country;
    private String genre;
    private LocalDate releaseDate;
    private String synopsis;
    private Boolean released; // 공개 여부
    private String certification; // 관람등급
}