package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.recommend.service.recenttag.RecommendTagBasedMovieJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.recenttag.RecommendTagBasedMovieNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendTagBasedMovieService {

    private final RecommendTagBasedMovieJpaService jpaService;
    private final RecommendTagBasedMovieNeo4jService neo4jService;

    // 가장 최근에 준 태그 기반 추천
    public List<MovieRecommendResponse> recommendMoviesByTag(Long userId) {
        Long recentMovieId = jpaService.findMostRecentRatedMovieId(userId);
        if (recentMovieId == null) return List.of();

        List<String> preferredTags = jpaService.findTagsOfMovieByUser(userId, recentMovieId);
        if (preferredTags.isEmpty()) return List.of();

        List<MovieEntity> allMovies = jpaService.findAllReleasedMovies();
        List<Long> ratedMovieIds = jpaService.findRatedMovieIdsByUser(userId);
        List<Long> movieIds = allMovies.stream().map(MovieEntity::getId).toList();
        Map<Long, Double> avgMap = jpaService.getAverageScoreMap(movieIds);

        Map<Long, List<String>> tagMap = neo4jService.findTagsByMovieIdList(movieIds);

        return allMovies.stream()
                .filter(movie -> !ratedMovieIds.contains(movie.getId()))
                .filter(movie -> {
                    List<String> movieTags = tagMap.getOrDefault(movie.getId(), List.of());
                    return !Collections.disjoint(movieTags, preferredTags);
                })
                .sorted(Comparator.comparingDouble(
                        movie -> -avgMap.getOrDefault(movie.getId(), 0.0)
                ))
                .limit(10)
                .map(movie -> MovieRecommendResponse.from(
                        movie,
                        avgMap.getOrDefault(movie.getId(), 0.0),
                        List.of(),
                        tagMap.getOrDefault(movie.getId(), List.of())
                ))
                .toList();
    }
}