package com.grepp.smartwatcha.app.controller.api.recommend;

import com.grepp.smartwatcha.app.service.RecommendLatestMovieService;
import com.grepp.smartwatcha.infra.response.recommend.MovieRecommendLatestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendLatestApiController {

    private final RecommendLatestMovieService recommendService;

    @GetMapping("/latest-movies")
    public List<MovieRecommendLatestResponse> getLatestMovies() {
        return recommendService.getTop10LatestMovies();
    }
}
