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
    private String genre; // 단순 문자열 저장 (복수 장르면 쉼표 구분)
    private LocalDate releaseDate;
    private String synopsis;
    private Boolean released; // 공개 여부 (true: 공개됨, false: 예정작)
}