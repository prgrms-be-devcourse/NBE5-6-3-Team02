package com.grepp.smartwatcha.app.controller.api.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.recommend.RecommendGenreBasedMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendPersonalMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendTagBasedMovieService;
import com.grepp.smartwatcha.app.model.recommend.RecommendUserBasedMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendApiController {

    private final RecommendPersonalMovieService recommendPersonalMovieService;
    private final RecommendUserBasedMovieService recommendUserBasedMovieService;
    private final RecommendGenreBasedMovieService recommendGenreBasedMovieService;
    private final RecommendTagBasedMovieService recommendTagBasedMovieService;

    // 개인화 추천
    @GetMapping("/personal")
    public List<MovieRecommendResponse> getPersonalMovies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return recommendPersonalMovieService.getTop10PersonalMovies(userId);
    }

    // 유저 기반 추천
    @GetMapping("/user-based")
    public List<MovieRecommendResponse> getUserBasedMovies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return recommendUserBasedMovieService.getTop10UserBasedMovies(userId);
    }

    // 장르 기반 추천
    @GetMapping("/genre")
    public List<MovieRecommendResponse> getGenreBasedMovies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return recommendGenreBasedMovieService.recommendMoviesByGenre(userId);
    }

    // 태그 기반 추천
    @GetMapping("/tag")
    public List<MovieRecommendResponse> getTagBasedMovies(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return recommendTagBasedMovieService.recommendMoviesByTag(userId);
    }
}