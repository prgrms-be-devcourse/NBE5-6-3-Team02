package com.grepp.smartwatcha.app.controller.api.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendUserBasedResponse;
import com.grepp.smartwatcha.app.model.recommend.RecommendUserBasedMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend/user-based")
public class RecommendUserBasedApiController {

    private final RecommendUserBasedMovieService recommendUserBasedMovieService;

    @GetMapping
    public List<MovieRecommendUserBasedResponse> getUserBasedRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return recommendUserBasedMovieService.getTop10UserBasedMovies(userId);
    }

//    @GetMapping("/{userId}")
//    public List<MovieRecommendUserBasedResponse> getUserBasedRecommendationsForTest(@PathVariable Long userId) {
//        return recommendUserBasedMovieService.getTop10UserBasedMovies(userId);
//    }
}