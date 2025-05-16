package com.grepp.smartwatcha.infra.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "movie_tag")
public class MovieTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private MovieEntity movie;

    @ManyToOne
    private TagEntity tag;

    @ManyToOne
    private UserEntity user;

    private LocalDateTime createdAt;

    public MovieTagEntity(UserEntity user, MovieEntity movie, TagEntity tag) {
        this.user = user;
        this.movie = movie;
        this.tag = tag;
        this.createdAt = LocalDateTime.now();
    }
}
