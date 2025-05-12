package com.grepp.smartwatcha.app.controller.web.details;

import com.grepp.smartwatcha.app.model.details.dto.MovieDetailsDTO;
import com.grepp.smartwatcha.app.model.details.dto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.dto.RatingRequestDto;
import com.grepp.smartwatcha.app.model.details.repository.RatingJpaRepository;
import com.grepp.smartwatcha.app.model.details.service.MovieJpaService;
import com.grepp.smartwatcha.app.model.details.service.RatingJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MovieDetailsController {

    private final MovieJpaService movieJpaService;
    private final RatingJpaService ratingJpaService;

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable Long id, Model model) {
        MovieDetailsDTO movie = movieJpaService.getMovieDetail(id);
        Double averageScore = movieJpaService.getAverageScore(id);
        List<RatingBarDto> ratingList = ratingJpaService.getRatingDistributionList(id);
        Map<Integer, Integer> ratingDistribution = ratingJpaService.getRatingDistribution(id);

        model.addAttribute("movie", movie);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("ratingDistribution", ratingDistribution);
        model.addAttribute("ratingBars", ratingList);

        return "movie/details";
    }

}

