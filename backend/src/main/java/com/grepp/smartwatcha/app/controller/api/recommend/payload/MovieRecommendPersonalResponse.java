package com.grepp.smartwatcha.app.controller.api.recommend.payload;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MovieRecommendPersonalResponse {
    private Long id;
    private String title;
    private LocalDateTime releaseDate;
    private String country;
    private String poster;
    private Double score;
    private List<String> genres;
    private List<String> tags;

    public static MovieRecommendPersonalResponse from(MovieEntity movie, double score, List<String> genres, List<String> tags) {
        return new MovieRecommendPersonalResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getCountry(),
                movie.getPoster(),
                score,
                genres,
                tags
        );
    }
}