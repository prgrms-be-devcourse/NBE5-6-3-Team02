package com.grepp.smartwatcha.app.controller.api.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendPersonalResponse;
import com.grepp.smartwatcha.app.model.recommend.RecommendPersonalMovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendPersonalApiController {

    private final RecommendPersonalMovieService recommendService;

    @GetMapping("/personal")
    public List<MovieRecommendPersonalResponse> getPersonalRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return recommendService.getTop10PersonalMovies(userId);
    }

//    @GetMapping("/personal")
//    public List<MovieRecommendPersonalResponse> getPersonalRecommendations(@RequestParam Long userId) {
//        return recommendService.getTop10PersonalMovies(userId);
//    }

}
