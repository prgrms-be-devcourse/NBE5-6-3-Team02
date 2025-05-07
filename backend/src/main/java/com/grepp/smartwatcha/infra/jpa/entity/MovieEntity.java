package com.grepp.smartwatcha.infra.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntity {
    @Id
    private Long id;

    private String title;
    private int year;
    private String country;
    private LocalDateTime createdAt;
    private boolean activated;
    private String poster;
    private Boolean isReleased; // 공개 여부
    private String certification; // 관람등급
}