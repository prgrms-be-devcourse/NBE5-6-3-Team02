package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendUserBasedResponse;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendUserBasedRatedJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendUserBasedRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendUserBasedMovieService {

    private final RecommendUserBasedRatedJpaService ratingService;
    private final RecommendUserBasedRatedNeo4jService graphService;
    private final MovieQueryJpaRepository movieQueryRepository;

    public List<MovieRecommendUserBasedResponse> getTop10UserBasedMovies(Long userId) {
        Map<Long, Double> finalScores = ratingService.calculateUserBasedScores(userId);

        List<Long> topMovieIds = finalScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        if (topMovieIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, MovieEntity> movieMap = movieQueryRepository.findByIdIn(topMovieIds).stream()
                .collect(Collectors.toMap(MovieEntity::getId, movie -> movie));

        List<MovieGenreTagResponse> genreTagResponseList =
                graphService.getGenreTagInfoByMovieIdList(topMovieIds);

        Map<Long, List<String>> genreMap = genreTagResponseList.stream()
                .collect(Collectors.toMap(MovieGenreTagResponse::getMovieId, MovieGenreTagResponse::getGenres));
        Map<Long, List<String>> tagMap = genreTagResponseList.stream()
                .collect(Collectors.toMap(MovieGenreTagResponse::getMovieId, MovieGenreTagResponse::getTags));

        return topMovieIds.stream()
                .map(movieId -> MovieRecommendUserBasedResponse.from(
                        movieMap.get(movieId),
                        finalScores.get(movieId),
                        genreMap.getOrDefault(movieId, List.of()),
                        tagMap.getOrDefault(movieId, List.of())
                ))
                .toList();
    }
}