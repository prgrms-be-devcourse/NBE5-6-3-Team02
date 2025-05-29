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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional("jpaTransactionManager")
public class RecommendPersonalRatedJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private final MovieQueryJpaRepository movieQueryRepository;


    public List<MovieEntity> findAllReleasedMovies() {
        return movieQueryRepository.findAllReleased();
    }

    public Map<String, Double> calculateGenrePreferences(Long userId, Map<Long, List<String>> genreMap) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);
        Map<String, List<Double>> genreScores = new HashMap<>();

        for (RatingEntity rating : ratings) {
            double weight = 1.0 / Math.sqrt(Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toDays() + 1);
            double weightedScore = rating.getScore() * weight;
            List<String> genres = genreMap.getOrDefault(rating.getMovie().getId(), List.of());

            for (String genre : genres) {
                genreScores.computeIfAbsent(genre, k -> new ArrayList<>()).add(weightedScore);
            }
        }

        return genreScores.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                ));
    }

    public Map<String, Double> calculateTagPreferences(Long userId, Map<Long, List<String>> tagMap) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);
        Map<String, List<Double>> tagScores = new HashMap<>();

        for (RatingEntity rating : ratings) {
            double weight = 1.0 / Math.sqrt(Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toDays() + 1);
            double weightedScore = rating.getScore() * weight;
            List<String> tags = tagMap.getOrDefault(rating.getMovie().getId(), List.of());

            for (String tag : tags) {
                tagScores.computeIfAbsent(tag, k -> new ArrayList<>()).add(weightedScore);
            }
        }

        return tagScores.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                ));
    }

    @Transactional(transactionManager = "jpaTransactionManager", readOnly = true)
    public List<Long> getRatedMovieIdsByUser(Long userId) {
        return ratingRepository.findByUserId(userId).stream()
                .map(r -> r.getMovie().getId())
                .distinct()
                .toList();
    }

}