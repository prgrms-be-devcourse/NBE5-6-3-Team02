package com.grepp.smartwatcha.app.model.recommend.service;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.RatingRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendPersonalRatedJpaService {

    private final RatingRepository ratingRepository;
    private final MovieQueryRepository movieQueryRepository;
    private final RecommendPersonalRatedNeo4jService graphService;

    @PersistenceContext
    private jakarta.persistence.EntityManager em;

    public List<MovieEntity> findAllReleasedMovies() {
        return movieQueryRepository.findAllReleased();
    }

    @Transactional("jpaTransactionManager")
    public Map<String, Double> calculateGenrePreferences(Long userId) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);

        Map<String, List<Double>> genreScores = new HashMap<>();

        for (RatingEntity rating : ratings) {
            double weight = 1.0 / (Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toDays() + 1);
            double weightedScore = rating.getScore() * weight;

            List<String> genres = graphService.getGenresByMovieId(rating.getMovie().getId());

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

    @Transactional("jpaTransactionManager")
    public Map<String, Double> calculateTagPreferences(Long userId) {
        List<RatingEntity> ratings = ratingRepository.findByUserId(userId);

        Map<String, List<Double>> tagScores = new HashMap<>();

        for (RatingEntity rating : ratings) {
            double weight = 1.0 / (Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toDays() + 1);
            double weightedScore = rating.getScore() * weight;

            List<String> tags = graphService.getTagsByMovieId(rating.getMovie().getId());

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
}