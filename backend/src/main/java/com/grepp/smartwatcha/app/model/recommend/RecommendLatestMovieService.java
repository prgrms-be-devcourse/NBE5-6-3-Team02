package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.model.recommend.service.RecommendLatestRatedJpaService;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendLatestResponse;
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
