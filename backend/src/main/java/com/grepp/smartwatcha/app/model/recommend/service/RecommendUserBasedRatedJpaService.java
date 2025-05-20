package com.grepp.smartwatcha.app.model.recommend.service;

import com.grepp.smartwatcha.app.model.recommend.repository.RatingRecommendJpaRepository;
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
public class RecommendUserBasedRatedJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private static final int K = 10;


    public Map<Long, Double> calculateUserBasedScores(Long userId) {
        List<RatingEntity> myRatings = ratingRepository.findByUserId(userId);
        if (myRatings.isEmpty()) return Map.of();

        Map<Long, Double> targetRatingMap = new HashMap<>();
        List<Long> targetMovieIds = new ArrayList<>();
        for (RatingEntity rating : myRatings) {
            Long movieId = rating.getMovie().getId();
            targetRatingMap.put(movieId, rating.getScore());
            targetMovieIds.add(movieId);
        }

        List<Long> candidateUserIds = ratingRepository.findUsersWithCommonRatedMovies(userId, targetMovieIds);
        if (candidateUserIds.isEmpty()) return Map.of();

        List<Long> userIdList = new ArrayList<>(candidateUserIds);
        userIdList.add(userId);

        List<RatingEntity> ratings = ratingRepository.findByUserIdIn(userIdList);

        Map<Long, List<RatingEntity>> userRatingsMap = new HashMap<>();
        for (RatingEntity rating : ratings) {
            userRatingsMap
                    .computeIfAbsent(rating.getUser().getId(), k -> new ArrayList<>())
                    .add(rating);
        }

        Map<Long, Double> similarityMap = new HashMap<>();
        for (Long otherUserId : candidateUserIds) {
            List<RatingEntity> otherRatings = userRatingsMap.get(otherUserId);
            if (otherRatings == null) continue;

            Map<Long, Double> otherRatingMap = new HashMap<>();
            for (RatingEntity rating : otherRatings) {
                otherRatingMap.put(rating.getMovie().getId(), rating.getScore());
            }

            double similarity = cosineSimilarity(targetRatingMap, otherRatingMap);
            if (similarity > 0) {
                similarityMap.put(otherUserId, similarity);
            }
        }

        List<Long> topKUserIds = similarityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(K)
                .map(Map.Entry::getKey)
                .toList();

        Map<Long, Double> predictedScores = new HashMap<>();
        Map<Long, Double> similaritySums = new HashMap<>();

        for (Long similarUserId : topKUserIds) {
            double similarity = similarityMap.get(similarUserId);
            List<RatingEntity> otherRatings = userRatingsMap.get(similarUserId);

            for (RatingEntity rating : otherRatings) {
                Long movieId = rating.getMovie().getId();
                if (targetRatingMap.containsKey(movieId)) continue;

                double timeWeight = 1.0 / Math.sqrt(Duration.between(rating.getCreatedAt(), LocalDateTime.now()).toDays() + 1);
                double weightedScore = rating.getScore() * similarity * timeWeight;

                predictedScores.merge(movieId, weightedScore, Double::sum);
                similaritySums.merge(movieId, similarity, Double::sum);
            }
        }

        Map<Long, Double> finalScores = new HashMap<>();
        for (Map.Entry<Long, Double> entry : predictedScores.entrySet()) {
            Long movieId = entry.getKey();
            double score = entry.getValue() / similaritySums.getOrDefault(movieId, 1.0);
            finalScores.put(movieId, score);
        }



        return finalScores;
    }

    private double cosineSimilarity(Map<Long, Double> a, Map<Long, Double> b) {
        Set<Long> common = new HashSet<>(a.keySet());
        common.retainAll(b.keySet());
        if (common.isEmpty()) return 0.0;

        double dotProduct = 0, normA = 0, normB = 0;
        for (Long key : common) {
            dotProduct += a.get(key) * b.get(key);
        }
        for (double v : a.values()) normA += v * v;
        for (double v : b.values()) normB += v * v;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));


    }
}