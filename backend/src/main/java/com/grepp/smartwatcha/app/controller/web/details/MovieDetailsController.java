package com.grepp.smartwatcha.app.controller.web.details;

import com.grepp.smartwatcha.app.model.details.dto.MovieDetailsDTO;
import com.grepp.smartwatcha.app.model.details.dto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.service.MovieJpaService;
import com.grepp.smartwatcha.app.model.details.service.MovieNeo4jService;
import com.grepp.smartwatcha.app.model.details.service.RatingJpaService;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MovieDetailsController {

    private final MovieJpaService movieJpaService;
    private final RatingJpaService ratingJpaService;
    private final MovieNeo4jService movieNeo4jService;

    @GetMapping("/movies/{id}")
    public String getMovieDetail(@PathVariable Long id, Model model) {
        MovieDetailsDTO movie = movieJpaService.getMovieDetail(id);
        Double averageScore = movieJpaService.getAverageScore(id);
        List<RatingBarDto> ratingList = ratingJpaService.getRatingDistributionList(id);
        Map<Integer, Integer> ratingDistribution = ratingJpaService.getRatingDistribution(id);
        MovieNode neo4jMovie = movieNeo4jService.getMovieWithAllRelations(id);

        // mysql db
        model.addAttribute("movie", movie);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("ratingDistribution", ratingDistribution);
        model.addAttribute("ratingBars", ratingList);
        // neo4j db
        model.addAttribute("no4jMovie", neo4jMovie);
        model.addAttribute("genres", neo4jMovie.getGenres());
        model.addAttribute("actors", neo4jMovie.getActors());
        model.addAttribute("directors", neo4jMovie.getDirectors());
        return "movie/details";
    }

}

