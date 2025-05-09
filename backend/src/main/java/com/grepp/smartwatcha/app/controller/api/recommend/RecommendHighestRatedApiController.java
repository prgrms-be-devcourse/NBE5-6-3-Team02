package com.grepp.smartwatcha.app.controller.api.recommend;


import com.grepp.smartwatcha.app.service.RecommendHighestRatedMovieService;
import com.grepp.smartwatcha.infra.response.recommend.MovieRecommendHighestRatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendHighestRatedApiController {

    private final RecommendHighestRatedMovieService recommendService;

    @GetMapping("/highest-rated")
    public List<MovieRecommendHighestRatedResponse> getTopRated() {
        return recommendService.getTop10HighestRatedMovies();
    }

}
