package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.service.userbased.RecommendUserBasedRatedJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.userbased.RecommendUserBasedRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendUserBasedMovieService {

    private final RecommendUserBasedRatedJpaService ratingService;
    private final RecommendUserBasedRatedNeo4jService graphService;
    private final MovieQueryJpaRepository movieQueryRepository;

    // 협업 필터링 기반 10개 영화 추천
    public List<MovieRecommendResponse> getTop10UserBasedMovies(Long userId) {
        Map<Long, Double> finalScores = ratingService.calculateUserBasedScores(userId);
        List<Long> topMovieIds = getTop10MovieIds(finalScores);

        if (topMovieIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, MovieEntity> movieMap = getMovieMap(topMovieIds);
        Map<Long, List<String>> genreMap = new HashMap<>();
        Map<Long, List<String>> tagMap = new HashMap<>();
        buildGenreAndTagMaps(topMovieIds, genreMap, tagMap);

        return buildResponseList(topMovieIds, finalScores, movieMap, genreMap, tagMap);
    }

    // 예측 점수 내림차순 정렬
    private List<Long> getTop10MovieIds(Map<Long, Double> scoreMap) {
        List<Map.Entry<Long, Double>> sortedEntries = new ArrayList<>(scoreMap.entrySet());
        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Long> result = new ArrayList<>();
        int count = Math.min(10, sortedEntries.size());
        for (int i = 0; i < count; i++) {
            result.add(sortedEntries.get(i).getKey());
        }
        return result;
    }

    // movieEntity 조회하여 map으로 매핑
    private Map<Long, MovieEntity> getMovieMap(List<Long> movieIds) {
        List<MovieEntity> movies = movieQueryRepository.findByIdIn(movieIds);
        Map<Long, MovieEntity> map = new HashMap<>();
        for (MovieEntity movie : movies) {
            map.put(movie.getId(), movie);
        }
        return map;
    }

    // 장르와 태그 조회하여 map으로 저장
    private void buildGenreAndTagMaps(List<Long> movieIds, Map<Long, List<String>> genreMap, Map<Long, List<String>> tagMap) {
        List<MovieGenreTagResponse> responses = graphService.getGenreTagInfoByMovieIdList(movieIds);
        for (MovieGenreTagResponse response : responses) {
            genreMap.put(response.getMovieId(), response.getGenres());
            tagMap.put(response.getMovieId(), response.getTags());
        }
    }

    // 영화별로 점수,장르태그 조합하여 DTO로 변환
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

            MovieRecommendResponse response = MovieRecommendResponse.from(movie, score, genres, tags);
            responses.add(response);
        }
        return responses;
    }
}