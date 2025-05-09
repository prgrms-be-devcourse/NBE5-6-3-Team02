package com.grepp.smartwatcha.infra.response.recommend;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieRecommendLatestResponse {

    private Long id;
    private String title;
    private int year;
    private String country;
    private String poster;

    public static MovieRecommendLatestResponse from(MovieEntity movie) {
        return new MovieRecommendLatestResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getCountry(),
                movie.getPoster()
        );
    }
}
