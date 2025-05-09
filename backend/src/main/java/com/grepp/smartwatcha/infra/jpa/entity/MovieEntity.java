package com.grepp.smartwatcha.infra.jpa.entity;

import com.grepp.smartwatcha.infra.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieEntity extends BaseEntity {
    @Id
    private Long id;

    private String title;
    private LocalDateTime releaseDate;
    private String country;
    private String poster;
    private Boolean isReleased; // 공개 여부
    private String certification; // 관람등급

    @Lob
    @Length(max=256)
    private String overview;

}