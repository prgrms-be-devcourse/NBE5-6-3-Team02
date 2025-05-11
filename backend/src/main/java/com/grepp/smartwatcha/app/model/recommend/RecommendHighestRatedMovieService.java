package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.model.recommend.service.RecommendHighestRatedJpaService;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
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

