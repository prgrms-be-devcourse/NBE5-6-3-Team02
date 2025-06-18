package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreDto;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.recommend.service.userbased.RecommendUserBasedRatedJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.userbased.RecommendUserBasedRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendUserBasedMovieService {

    private final RecommendUserBasedRatedJpaService jpaService;
    private final RecommendUserBasedRatedNeo4jService neo4jService;

    // 유사한 사용자의 평가 기반으로 추천
    public List<MovieRecommendResponse> getTop10UserBasedMovies(Long userId) {
        Map<Long, Double> finalScores = jpaService.calculateUserBasedScores(userId);
        List<Long> topMovieIds = getTop10MovieIds(finalScores);

        if (topMovieIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<String>> genreMap = neo4jService.getGenresByMovieIdList(topMovieIds)
                .stream()
                .collect(Collectors.toMap(MovieGenreDto::getMovieId, MovieGenreDto::getGenres));

        Map<Long, List<String>> tagMap = jpaService.getTagMapByMovieIds(topMovieIds);

        Map<Long, MovieEntity> movieMap = jpaService.getMoviesByIds(topMovieIds);

        List<MovieRecommendResponse> result = buildResponseList(topMovieIds, finalScores, movieMap, genreMap, tagMap);

        return result;
    }

    // 추천 점수 상위 10개 영화만 추출하여 반환
    private List<Long> getTop10MovieIds(Map<Long, Double> scoreMap) {
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }

    // Response형태로 구성
    private List<MovieRecommendResponse> buildResponseList(
            List<Long> movieIds,
            Map<Long, Double> scoreMap,
            Map<Long, MovieEntity> movieMap,
            Map<Long, List<String>> genreMap,
            Map<Long, List<String>> tagMap
    ) {
        List<MovieRecommendResponse> responses = new ArrayList<>();
        for (Long movieId : movieIds) {
            MovieEntity movie = movieMap.get(movieId);
            double score = scoreMap.getOrDefault(movieId, 0.0);
            List<String> genres = genreMap.getOrDefault(movieId, List.of());
            List<String> tags = tagMap.getOrDefault(movieId, List.of());
            responses.add(MovieRecommendResponse.from(movie, score, genres, tags));
        }
        return responses;
    }
}