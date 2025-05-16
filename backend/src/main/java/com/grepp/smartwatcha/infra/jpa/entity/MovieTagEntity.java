package com.grepp.smartwatcha.infra.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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
}
