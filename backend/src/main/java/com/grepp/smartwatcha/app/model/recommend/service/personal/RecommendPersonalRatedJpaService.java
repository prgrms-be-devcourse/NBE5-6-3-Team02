package com.grepp.smartwatcha.app.model.recommend.service.personal;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieTagRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.RatingRecommendJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(value = "jpaTransactionManager", readOnly = true)
public class RecommendPersonalRatedJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private final MovieQueryJpaRepository movieQueryRepository;
    private final MovieTagRepository movieTagRepository;

    // 출시된 영화 목록 조회
    public List<MovieEntity> findAllReleasedMovies() {
        return movieQueryRepository.findAllReleased();
    }

    // 사용자가 평가한 영화 반환
    public List<Long> getRatedMovieIdsByUser(Long userId) {
        return ratingRepository.findByUserId(userId).stream()
                .map(r -> r.getMovie().getId())
                .distinct()
                .toList();
    }

    // 장르별 선호도를 별점과 시간 가중치 반영해서 계산
    public Map<String, Double> calculateGenrePreferences(Long userId, Map<Long, List<String>> genreMap) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);
        Map<String, List<Double>> genreScores = new HashMap<>();
        for (RatingEntity rating : ratings) {
            double weighted = getWeightedScore(rating);
            for (String genre : genreMap.getOrDefault(rating.getMovie().getId(), List.of())) {
                genreScores.computeIfAbsent(genre, k -> new ArrayList<>()).add(weighted);
            }
        }
        return averageMap(genreScores);
    }

    // 태그별 선호도 계산
    public Map<String, Double> calculateTagPreferencesByUserOnly(Long userId, List<Long> ratedMovieIds) {
        List<Object[]> rows = movieTagRepository.findTagNamesByUserAndMovieIds(userId, ratedMovieIds);
        Map<Long, List<String>> tagMap = new HashMap<>();
        for (Object[] row : rows) {
            Long movieId = (Long) row[0];
            String tagName = (String) row[1];
            tagMap.computeIfAbsent(movieId, k -> new ArrayList<>()).add(tagName);
        }

        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);
        Map<String, List<Double>> tagScores = new HashMap<>();
        for (RatingEntity rating : ratings) {
            double weighted = getWeightedScore(rating);
            List<String> tags = tagMap.getOrDefault(rating.getMovie().getId(), List.of());

            for (String tag : tags) {
                tagScores.computeIfAbsent(tag, k -> new ArrayList<>()).add(weighted);
            }
        }

        Map<String, Double> result = averageMap(tagScores);
        return result;
    }


    // 데이터 가공
    private Map<String, Double> averageMap(Map<String, List<Double>> map) {
        Map<String, Double> result = new HashMap<>();
        for (var entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        }
        return result;
    }

    // 별점 시간 기반 가중치를 곱해 계산
    private double getWeightedScore(RatingEntity rating) {
        long daysSince = Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toDays();
        double weight = 1.0 / Math.sqrt(daysSince + 1);
        return rating.getScore() * weight;
    }
}