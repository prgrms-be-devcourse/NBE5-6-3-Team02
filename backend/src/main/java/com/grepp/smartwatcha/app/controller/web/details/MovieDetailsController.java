package com.grepp.smartwatcha.app.controller.web.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.MovieDetailsDto;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.SimilarMovieDto;
import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.TagCountRequestDto;
import com.grepp.smartwatcha.app.model.details.service.GenreRecommendService;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.InterestJpaService;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.MovieJpaService;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.RatingJpaService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.MovieNeo4jService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.TagNeo4jService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final InterestJpaService interestJpaService;
    private final GenreRecommendService genreRecommendService;


    @GetMapping("/{id}")
    public String getMovieDetail(
            @PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {

        MovieDetailsDto movie = movieJpaService.getMovieDetail(id);
        Double averageScore = movieJpaService.getAverageScore(id);
        List<RatingBarDto> ratingList = ratingJpaService.getRatingDistributionList(id);
        Map<Integer, Integer> ratingDistribution = ratingJpaService.getRatingDistribution(id);
        MovieNode neo4jMovie = movieNeo4jService.getMovieWithAllRelations(id);
        List<TagCountRequestDto> topTags = tagNeo4jService.getTop6Tags(id);

        List<SimilarMovieDto> similarMovies = genreRecommendService.getGenreSimilarMovies(id);


        // mysql db
        model.addAttribute("movie", movie);
        model.addAttribute("averageScore", averageScore);
        model.addAttribute("ratingDistribution", ratingDistribution);
        model.addAttribute("ratingBars", ratingList);
        model.addAttribute("similarMovies", similarMovies);

        // neo4j db
        model.addAttribute("no4jMovie", neo4jMovie);
        model.addAttribute("genres", neo4jMovie.getGenres());
        model.addAttribute("actors", neo4jMovie.getActors());
        model.addAttribute("directors", neo4jMovie.getDirectors());
        model.addAttribute("topTags", topTags);

        if (userDetails == null) {
            return "movie/guest-details"; // 비로그인 사용자용 페이지
        }

        UserEntity userEntity = userDetails.getUser();
        if (userEntity == null || userEntity.getId() == null) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }


        Long userId = userEntity.getId();
        Integer userRating = ratingJpaService.getUserRating(userId, id);
        Status interestStatus = interestJpaService.getInterestStatus(userId, id);
        model.addAttribute("userRating", userRating);
        model.addAttribute("interestStatus", interestStatus);
        model.addAttribute("userId", userId);
        model.addAttribute("user", userDetails);
        return "movie/member-details";  // 로그인한 사용자용 페이지
    }
}


