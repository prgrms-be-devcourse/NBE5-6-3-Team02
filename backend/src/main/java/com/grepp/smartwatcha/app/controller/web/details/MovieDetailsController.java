package com.grepp.smartwatcha.app.controller.web.details;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.MovieDetailsDTO;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.Neo4jTagDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.MovieJpaService;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.RatingJpaService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.MovieNeo4jService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.TagNeo4jService;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieDetailsController {

    private final MovieJpaService movieJpaService;
    private final RatingJpaService ratingJpaService;
    private final MovieNeo4jService movieNeo4jService;
    private final TagNeo4jService tagNeo4jService;

    @GetMapping("/{id}")
    public String getMovieDetail(
            @PathVariable Long id, Model model,@AuthenticationPrincipal UserDetails userDetails) {

        MovieDetailsDTO movie = movieJpaService.getMovieDetail(id);
        Double averageScore = movieJpaService.getAverageScore(id);
        List<RatingBarDto> ratingList = ratingJpaService.getRatingDistributionList(id);
        Map<Integer, Integer> ratingDistribution = ratingJpaService.getRatingDistribution(id);
        MovieNode neo4jMovie = movieNeo4jService.getMovieWithAllRelations(id);
        List<Neo4jTagDto> topTags = tagNeo4jService.getTop6Tags(id);


        // 여기서 user에대한 정보 받아와야 하나?!!

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
        System.out.println(">>> topTags = " + topTags);
        model.addAttribute("topTags", topTags);

        if (userDetails != null) {
            model.addAttribute("user", userDetails);
            return "movie/member-details";  // 로그인한 사용자용 페이지
        } else {
            return "movie/guest-details";   // 비로그인 사용자용 페이지
        }
    }
}

