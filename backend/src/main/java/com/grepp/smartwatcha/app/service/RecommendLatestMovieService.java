package com.grepp.smartwatcha.app.service;

import com.grepp.smartwatcha.app.service.recommend.RecommendLatestRatedJpaService;
import com.grepp.smartwatcha.infra.response.recommend.MovieRecommendLatestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendLatestMovieService {

    private final RecommendLatestRatedJpaService jpaService;

    public List<MovieRecommendLatestResponse> getTop10LatestMovies() {
        return jpaService.getTop10LatestMoviesWithGenres();
    }
}
