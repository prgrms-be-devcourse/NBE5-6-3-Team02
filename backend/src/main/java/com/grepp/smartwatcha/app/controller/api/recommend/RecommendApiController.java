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

    private final RecommendPersonalMovieService recommendService;
    private final RecommendHighestRatedMovieService recommendService1;
    private final RecommendLatestMovieService recommendService2;
    private final RecommendUserBasedMovieService recommendUserBasedMovieService;
    // 별점 상위 10개 영화 반환
    @GetMapping("/highest-rated")
    public List<MovieRecommendHighestRatedResponse> getTopRated() {
        return recommendService1.getTop10HighestRatedMovies();
    }

    @GetMapping("/latest-movies")
    public List<MovieRecommendLatestResponse> getLatestMovies() {
        return recommendService2.getTop10LatestMovies();
    }

//    @GetMapping("/personal")
//    public List<MovieRecommendPersonalResponse> getPersonalRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
//        Long userId = Long.parseLong(userDetails.getUsername());
//        return recommendService.getTop10PersonalMovies(userId);
//    }

    @GetMapping("/content/{userId}")
    public List<MovieRecommendPersonalResponse> getPersonalRecommendations(@PathVariable Long userId) {
        return recommendService.getTop10PersonalMovies(userId);
    }

//    @GetMapping("/collaboration")
//    public List<MovieRecommendUserBasedResponse> getUserBasedRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
//        Long userId = Long.parseLong(userDetails.getUsername());
//        return recommendUserBasedMovieService.getTop10UserBasedMovies(userId);
//    }

    @GetMapping("/collaboration/{userId}")
    public List<MovieRecommendUserBasedResponse> getUserBasedRecommendationsForTest(@PathVariable Long userId) {
        return recommendUserBasedMovieService.getTop10UserBasedMovies(userId);
    }
}