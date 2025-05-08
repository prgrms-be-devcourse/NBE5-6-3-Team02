package com.grepp.smartwatcha.app.details.controller;

import com.grepp.smartwatcha.app.details.service.MovieService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class MovieDetailsController {

    private final MovieService movieService;

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable Long id, Model model) {
        MovieEntity movie = movieService.getMovieDetail(id);
        Double averageScore = movieService.getAverageScore(id);

        model.addAttribute("movie", movie);
        model.addAttribute("averageScore", averageScore);

        return "movie/detail"; // templates/movie/detail.html로 매핑
    }
}

