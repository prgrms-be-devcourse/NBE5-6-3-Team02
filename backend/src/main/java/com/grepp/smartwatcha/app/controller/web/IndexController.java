package com.grepp.smartwatcha.app.controller.web;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
import com.grepp.smartwatcha.app.model.index.IndexService;
import com.grepp.smartwatcha.app.model.index.dto.IndexMovieDto;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendHighestRatedJpaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;
    private final RecommendHighestRatedJpaService recommendService;
    
    @GetMapping("/")
    public String index(
            Model model) {

        List<IndexMovieDto> newMovies = indexService.findByReleaseDate();
        model.addAttribute("newMovies", newMovies);

        List<IndexMovieDto> randomMovies = indexService.findByRandom();
        model.addAttribute("randomMovies", randomMovies);

        List<IndexMovieDto> lightMovies = indexService.findLightMovies();
        model.addAttribute("lightMovies", lightMovies);

        List<MovieRecommendHighestRatedResponse> top10Movies = recommendService.getTop10RatedMovies();
        model.addAttribute("top10Movies", top10Movies);

        return "index";
    }
    
}
