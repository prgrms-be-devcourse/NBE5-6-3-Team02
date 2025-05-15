package com.grepp.smartwatcha.app.controller.web;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
import com.grepp.smartwatcha.app.model.index.IndexService;
import com.grepp.smartwatcha.app.model.index.dto.IndexMovieDto;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendHighestRatedJpaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    private final IndexService indexService;
    private final RecommendHighestRatedJpaService recommendService;
    
    @GetMapping("/")
    public String index(Model model) {
        List<IndexMovieDto> newMovies = indexService.findByReleaseDate();
        List<IndexMovieDto> randomMovies = indexService.findByRandom();
        List<IndexMovieDto> lightMovies = indexService.findLightMovies();
        List<MovieRecommendHighestRatedResponse> top10Movies = recommendService.getTop10RatedMovies();

        model.addAttribute("newMovies", newMovies);
        model.addAttribute("randomMovies", randomMovies);
        model.addAttribute("lightMovies", lightMovies);
        model.addAttribute("top10Movies", top10Movies);

        return "index";
    }
}
