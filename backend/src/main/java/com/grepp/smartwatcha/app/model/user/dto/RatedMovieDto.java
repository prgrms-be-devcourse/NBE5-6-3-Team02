package com.grepp.smartwatcha.app.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatedMovieDto {
    private Long movieId;
    private String title;
    private String poster;
    private Double score;

    public static RatedMovieDto from(com.grepp.smartwatcha.infra.jpa.entity.RatingEntity entity) {
        return new RatedMovieDto(
            entity.getMovie().getId(),
            entity.getMovie().getTitle(),
            entity.getMovie().getPoster(),
            entity.getScore()
        );
    }
} 