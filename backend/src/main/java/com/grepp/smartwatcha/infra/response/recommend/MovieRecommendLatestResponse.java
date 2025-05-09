package com.grepp.smartwatcha.infra.response.recommend;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MovieRecommendLatestResponse {

    private Long id;
    private String title;
    private int year;
    private String country;
    private String poster;
    private Double avgScore;
    private List<String> genre;

    public static MovieRecommendLatestResponse from(MovieEntity movie, Double avgScore, List<String> genres) {
        return new MovieRecommendLatestResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getCountry(),
                movie.getPoster(),
                avgScore,
                genres
        );
    }
}
