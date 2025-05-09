package com.grepp.smartwatcha.app.details.controller;

import com.grepp.smartwatcha.app.details.dto.MovieDetailsDTO;
import com.grepp.smartwatcha.app.details.service.MovieService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MovieDetailsController {

    private final MovieService movieService;

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable Long id, Model model) {
        MovieDetailsDTO movie = movieService.getMovieDetail(id);
        Double averageScore = movieService.getAverageScore(id);

        // ⭐ 별점 분포 데이터 예시 (임시 더미 데이터)
        Map<Integer, Integer> ratingDistribution = new HashMap<>();
        ratingDistribution.put(1, 10);
        ratingDistribution.put(2, 30);
        ratingDistribution.put(3, 50);
        ratingDistribution.put(4, 80);
        ratingDistribution.put(5, 100);

        model.addAttribute("movie", movie);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("ratingDistribution", ratingDistribution); // ✅ 추가

        return "movie/details";
    }
}

