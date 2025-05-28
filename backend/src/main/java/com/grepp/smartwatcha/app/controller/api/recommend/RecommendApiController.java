package com.grepp.smartwatcha.app.controller.api.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendLatestResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendPersonalResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendUserBasedResponse;
import com.grepp.smartwatcha.app.model.recommend.RecommendHighestRatedMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendLatestMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendPersonalMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendUserBasedMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendApiController {

    private final RecommendPersonalMovieService personalService;
    private final RecommendHighestRatedMovieService ratedService;
    private final RecommendLatestMovieService LatestService;
    private final RecommendUserBasedMovieService UserBasedService;
    // 별점 상위 10개 영화 반환
    @GetMapping("/highest-rated")
    public List<MovieRecommendHighestRatedResponse> getTopRated() {
        return ratedService.getTop10HighestRatedMovies();
    }

    @GetMapping("/latest-movies")
    public List<MovieRecommendLatestResponse> getLatestMovies() {
        return LatestService.getTop10LatestMovies();
    }

//    @GetMapping("/personal")
//    public List<MovieRecommendPersonalResponse> getPersonalRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
//        Long userId = Long.parseLong(userDetails.getUsername());
//        return recommendService.getTop10PersonalMovies(userId);
//    }

    @GetMapping("/content/{userId}")
    public List<MovieRecommendPersonalResponse> getPersonalRecommendations(@PathVariable Long userId) {
        return personalService.getTop10PersonalMovies(userId);
    }

//    @GetMapping("/collaboration")
//    public List<MovieRecommendUserBasedResponse> getUserBasedRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
//        Long userId = Long.parseLong(userDetails.getUsername());
//        return recommendUserBasedMovieService.getTop10UserBasedMovies(userId);
//    }

    @GetMapping("/collaboration/{userId}")
    public List<MovieRecommendUserBasedResponse> getUserBasedRecommendationsForTest(@PathVariable Long userId) {
        return UserBasedService.getTop10UserBasedMovies(userId);
    }
}