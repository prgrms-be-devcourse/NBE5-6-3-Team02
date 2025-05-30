package com.grepp.smartwatcha.app.model.recommend.service.personal;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
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
@Transactional("jpaTransactionManager")
public class RecommendPersonalRatedJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private final MovieQueryJpaRepository movieQueryRepository;

    // 공개된 영화 조회
    public List<MovieEntity> findAllReleasedMovies() {
        return movieQueryRepository.findAllReleased();
    }

    // 장르별 선호 점수 계산 후 평균값을 맵으로 생성
    public Map<String, Double> calculateGenrePreferences(Long userId, Map<Long, List<String>> genreMap) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);
        Map<String, List<Double>> genreScores = new HashMap<>();

        for (RatingEntity rating : ratings) {
            double weightedScore = getWeightedScore(rating);
            List<String> genres = genreMap.getOrDefault(rating.getMovie().getId(), List.of());

            for (String genre : genres) {
                genreScores.computeIfAbsent(genre, key -> new ArrayList<>()).add(weightedScore);
            }
        }
        
        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : genreScores.entrySet()) {
            double average = calculateAverage(entry.getValue());
            result.put(entry.getKey(), average);
        }

        return result;
    }

    // 태그별 선호 점수 계산 후 평균값을 맵으로 생성
    public Map<String, Double> calculateTagPreferences(Long userId, Map<Long, List<String>> tagMap) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);
        Map<String, List<Double>> tagScores = new HashMap<>();

        for (RatingEntity rating : ratings) {
            double weightedScore = getWeightedScore(rating);
            List<String> tags = tagMap.getOrDefault(rating.getMovie().getId(), List.of());

            for (String tag : tags) {
                tagScores.computeIfAbsent(tag, key -> new ArrayList<>()).add(weightedScore);
            }
        }

        Map<String, Double> result = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : tagScores.entrySet()) {
            double average = calculateAverage(entry.getValue());
            result.put(entry.getKey(), average);
        }

        return result;
    }

    // 사용자가 평가한 id 목록 반환
    public List<Long> getRatedMovieIdsByUser(Long userId) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);
        Set<Long> movieIds = new HashSet<>();

        for (RatingEntity rating : ratings) {
            movieIds.add(rating.getMovie().getId());
        }

        return new ArrayList<>(movieIds);
    }

    // 시간에 따른 가중치 적용 후 점수 계산
    private double getWeightedScore(RatingEntity rating) {
        long daysSince = Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toDays();
        double weight = 1.0 / Math.sqrt(daysSince + 1);
        return rating.getScore() * weight;
    }

    // 리스트의 평균값 계산
    private double calculateAverage(List<Double> values) {
        if (values.isEmpty()) return 0.0;

        double sum = 0.0;
        for (Double v : values) {
            sum += v;
        }

        return sum / values.size();
    }
}