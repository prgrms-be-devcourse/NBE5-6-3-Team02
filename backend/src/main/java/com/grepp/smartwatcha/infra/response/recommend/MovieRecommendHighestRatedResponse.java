package com.grepp.smartwatcha.infra.response.recommend;


import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class MovieRecommendHighestRatedResponse {

    private Long id;
    private String title;
    private LocalDateTime ReleaseDate;
    private String country;
    private String poster;
    private Double avgScore;
    private List<String> genres;

    public static MovieRecommendHighestRatedResponse from(MovieEntity movie, Double avgScore, List<String> genres) {
        return new MovieRecommendHighestRatedResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getCountry(),
                movie.getPoster(),
                avgScore,
                genres
        );
    }
}
