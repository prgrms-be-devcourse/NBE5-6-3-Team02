package com.grepp.smartwatcha.app.controller.web.details;

import com.grepp.smartwatcha.app.model.details.dto.MovieDetailsDTO;
import com.grepp.smartwatcha.app.model.details.service.MovieJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MovieDetailsController {

    private final MovieJpaService movieJpaService;

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable Long id, Model model) {
        MovieDetailsDTO movie = movieJpaService.getMovieDetail(id);
        Double averageScore = movieJpaService.getAverageScore(id);

        model.addAttribute("movie", movie);
        model.addAttribute("averageScore", averageScore);

        return "movie/details";
    }
}

