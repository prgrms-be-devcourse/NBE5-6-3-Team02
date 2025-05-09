package com.grepp.smartwatcha.app.service;

import com.grepp.smartwatcha.app.service.recommend.RecommendHighestRatedJpaService;
import com.grepp.smartwatcha.infra.response.recommend.MovieRecommendHighestRatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendHighestRatedMovieService {
    private final RecommendHighestRatedJpaService jpaService;

    public List<MovieRecommendHighestRatedResponse> getTop10HighestRatedMovies() {
        return jpaService.getTop10RatedMovies();
    }
}

