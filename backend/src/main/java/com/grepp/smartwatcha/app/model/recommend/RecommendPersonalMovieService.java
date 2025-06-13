package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.recommend.service.personal.RecommendPersonalRatedJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.personal.RecommendPersonalRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager", readOnly = true)
public class RecommendPersonalMovieService {

    private final RecommendPersonalRatedJpaService ratingService;
    private final RecommendPersonalRatedNeo4jService graphService;

    // 콘텐츠 기반으로 영화 추천 10개 반환
    public List<MovieRecommendResponse> getTop10PersonalMovies(Long userId) {
        List<MovieEntity> allReleasedMovies = ratingService.findAllReleasedMovies();
        List<Long> ratedMovieIds = ratingService.getRatedMovieIdsByUser(userId);

        if (ratedMovieIds.isEmpty()) {
            return List.of();
        }

        List<Long> movieIds = extractMovieIds(allReleasedMovies);
        Map<Long, List<String>> genreMap = buildGenreMap(movieIds);
        Map<Long, List<String>> tagMap = buildTagMap(movieIds);

        Map<String, Double> genreScores = ratingService.calculateGenrePreferences(userId, genreMap);
        Map<String, Double> tagScores = ratingService.calculateTagPreferences(userId, tagMap);

        List<AbstractMap.SimpleEntry<MovieEntity, Double>> scored = calculateScores(
                allReleasedMovies, ratedMovieIds, genreMap, tagMap, genreScores, tagScores
        );

        return convertToResponse(scored, genreMap, tagMap);
    }

    // 영화 id 추출
    private List<Long> extractMovieIds(List<MovieEntity> movies) {
        List<Long> ids = new ArrayList<>();
        for (MovieEntity movie : movies) {
            ids.add(movie.getId());
        }
        return ids;
    }

    // 영화 장르 조회하고 영화 별 장르 맵 생성
    private Map<Long, List<String>> buildGenreMap(List<Long> movieIds) {
        List<MovieGenreTagResponse> responses = graphService.getGenreTagInfoByMovieIdList(movieIds);
        Map<Long, List<String>> map = new HashMap<>();
        for (MovieGenreTagResponse response : responses) {
            map.put(response.getMovieId(), response.getGenres());
        }
        return map;
    }

    // 태그 정보 조회하고 태그 맵 생성
    private Map<Long, List<String>> buildTagMap(List<Long> movieIds) {
        List<MovieGenreTagResponse> responses = graphService.getGenreTagInfoByMovieIdList(movieIds);
        Map<Long, List<String>> map = new HashMap<>();
        for (MovieGenreTagResponse response : responses) {
            map.put(response.getMovieId(), response.getTags());
        }
        return map;
    }

    // 각 영화의 장르, 태그 선호도 계산 후 영화에 점수 부여
    private List<AbstractMap.SimpleEntry<MovieEntity, Double>> calculateScores(
            List<MovieEntity> movies,
            List<Long> ratedIds,
            Map<Long, List<String>> genreMap,
            Map<Long, List<String>> tagMap,
            Map<String, Double> genreScores,
            Map<String, Double> tagScores
    ) {
        List<AbstractMap.SimpleEntry<MovieEntity, Double>> result = new ArrayList<>();

        for (MovieEntity movie : movies) {
            if (ratedIds.contains(movie.getId())) continue;

            List<String> genres = genreMap.getOrDefault(movie.getId(), List.of());
            List<String> tags = tagMap.getOrDefault(movie.getId(), List.of());

            double genreAvg = calculateAverage(genres, genreScores);
            double tagAvg = calculateAverage(tags, tagScores);

            double score = (genreAvg + tagAvg) * 100;
            result.add(new AbstractMap.SimpleEntry<>(movie, score));
        }

        result.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return result;
    }

    // 장르와 태그에 해당하는 점수 평균 계산
    private double calculateAverage(List<String> keys, Map<String, Double> scores) {
        if (keys.isEmpty()) return 0.0;

        double sum = 0.0;
        for (String key : keys) {
            sum += scores.getOrDefault(key, 0.0);
        }
        return sum / keys.size();
    }

    // 영화 리스트를 DTO로 변환
    private List<MovieRecommendResponse> convertToResponse(
            List<AbstractMap.SimpleEntry<MovieEntity, Double>> scored,
            Map<Long, List<String>> genreMap,
            Map<Long, List<String>> tagMap
    ) {
        List<MovieRecommendResponse> result = new ArrayList<>();
        int count = Math.min(10, scored.size());

        for (int i = 0; i < count; i++) {
            MovieEntity movie = scored.get(i).getKey();
            double score = scored.get(i).getValue();

            List<String> genres = genreMap.getOrDefault(movie.getId(), List.of());
            List<String> tags = tagMap.getOrDefault(movie.getId(), List.of());

            result.add(MovieRecommendResponse.from(movie, score, genres, tags));
        }

        return result;
    }
}