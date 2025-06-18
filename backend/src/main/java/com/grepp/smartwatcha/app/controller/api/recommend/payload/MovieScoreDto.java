package com.grepp.smartwatcha.app.controller.api.recommend.payload;


import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieScoreDto {
    private MovieEntity movie;
    private double score;
}
