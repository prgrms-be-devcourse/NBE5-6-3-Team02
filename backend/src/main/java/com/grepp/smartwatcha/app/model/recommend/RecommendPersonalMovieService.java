package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendPersonalResponse;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendPersonalRatedJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendPersonalRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendPersonalMovieService {

    private final RecommendPersonalRatedJpaService ratingService;
    private final RecommendPersonalRatedNeo4jService graphService;

    public List<MovieRecommendPersonalResponse> getTop10PersonalMovies(Long userId) {
        Map<String, Double> genreScoreMap = ratingService.calculateGenrePreferences(userId);
        Map<String, Double> tagScoreMap = ratingService.calculateTagPreferences(userId);

        List<MovieEntity> candidates = ratingService.findAllReleasedMovies();

        return candidates.stream()
                .map(movie -> {
                    List<String> genres = graphService.getGenresByMovieId(movie.getId());
                    List<String> tags = graphService.getTagsByMovieId(movie.getId());

                    double score = genres.stream().mapToDouble(g -> genreScoreMap.getOrDefault(g, 0.0)).average().orElse(0.0)
                            + tags.stream().mapToDouble(t -> tagScoreMap.getOrDefault(t, 0.0)).average().orElse(0.0);

                    return new AbstractMap.SimpleEntry<>(movie, score);
                })
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .map(e -> MovieRecommendPersonalResponse.from(e.getKey(), e.getValue(),
                        graphService.getGenresByMovieId(e.getKey().getId()),
                        graphService.getTagsByMovieId(e.getKey().getId())))
                .collect(Collectors.toList());
    }
}