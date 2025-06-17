package com.grepp.smartwatcha.app.model.recommend.service.userbased;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieCreatedAtResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRatingScoreDto;
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
@Transactional(value = "jpaTransactionManager", readOnly = true)
public class RecommendUserBasedRatedJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private static final int K = 10;

    // 유사한 사용자들의 평점 기반으로 영화별 예측 평점 계산
    public Map<Long, Double> calculateUserBasedScores(Long userId) {
        List<RatingEntity> myRatings = ratingRepository.findByUserId(userId);
        if (myRatings.isEmpty()) return Map.of();

        Map<Long, Double> targetRatingMap = buildRatingMap(myRatings);
        List<Long> targetMovieIds = extractMovieIds(myRatings);

        List<Long> candidateUserIds = ratingRepository.findUsersWithCommonRatedMovies(userId, targetMovieIds);
        if (candidateUserIds.isEmpty()) return Map.of();

        List<Long> userIdList = new ArrayList<>(candidateUserIds);
        userIdList.add(userId);

        List<MovieRatingScoreDto> ratingScores = ratingRepository.findRatingScoresByUserIdIn(userIdList);
        Map<Long, Map<Long, Double>> userRatingScoreMap = groupScoresByUser(ratingScores);

        Map<Long, Double> similarityMap = calculateSimilarities(candidateUserIds, targetRatingMap, userRatingScoreMap);
        List<Long> topKUserIds = getTopKSimilarUsers(similarityMap);

        List<MovieCreatedAtResponse> topKRatingDtos = ratingRepository.findRatingProjectionsByUserIdIn(topKUserIds);
        Map<Long, List<MovieCreatedAtResponse>> topKUserRatingsMap = groupByUser(topKRatingDtos);

        return predictScores(topKUserIds, targetRatingMap, topKUserRatingsMap, similarityMap);
    }

    // 평가한 영화 평점을 map으로 변환
    private Map<Long, Double> buildRatingMap(List<RatingEntity> ratings) {
        Map<Long, Double> ratingMap = new HashMap<>();
        for (RatingEntity rating : ratings) {
            ratingMap.put(rating.getMovie().getId(), rating.getScore());
        }
        return ratingMap;
    }

    // 평가한 영화의 id 추출
    private List<Long> extractMovieIds(List<RatingEntity> ratings) {
        List<Long> ids = new ArrayList<>();
        for (RatingEntity rating : ratings) {
            ids.add(rating.getMovie().getId());
        }
        return ids;
    }

    private Map<Long, Map<Long, Double>> groupScoresByUser(List<MovieRatingScoreDto> list) {
        Map<Long, Map<Long, Double>> map = new HashMap<>();
        for (MovieRatingScoreDto dto : list) {
            map.computeIfAbsent(dto.getUserId(), k -> new HashMap<>())
                    .put(dto.getMovieId(), dto.getScore());
        }
        return map;
    }

    // 평점 리스트를 id 기준으로 그룹핑
    private Map<Long, List<MovieCreatedAtResponse>> groupByUser(List<MovieCreatedAtResponse> list) {
        Map<Long, List<MovieCreatedAtResponse>> map = new HashMap<>();
        for (MovieCreatedAtResponse dto : list) {
            map.computeIfAbsent(dto.getUserId(), k -> new ArrayList<>()).add(dto);
        }
        return map;
    }

    // 유저와의 유사도 계산
    private Map<Long, Double> calculateSimilarities(
            List<Long> candidateUserIds,
            Map<Long, Double> targetRatingMap,
            Map<Long, Map<Long, Double>> userRatingScoreMap) {

        Map<Long, Double> similarityMap = new HashMap<>();

        for (Long otherUserId : candidateUserIds) {
            Map<Long, Double> otherRatingMap = userRatingScoreMap.get(otherUserId);
            if (otherRatingMap == null) continue;

            double similarity = cosineSimilarity(targetRatingMap, otherRatingMap);
            if (similarity > 0) {
                similarityMap.put(otherUserId, similarity);
            }
        }
        return similarityMap;
    }

    // 유사도 기준 k명 추출
    private List<Long> getTopKSimilarUsers(Map<Long, Double> similarityMap) {
        return similarityMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(K)
                .map(Map.Entry::getKey)
                .toList();
    }

    // 유사한 유저들의 영화별 예측 평점 계산
    private Map<Long, Double> predictScores(
            List<Long> topKUserIds,
            Map<Long, Double> targetRatingMap,
            Map<Long, List<MovieCreatedAtResponse>> userRatingsMap,
            Map<Long, Double> similarityMap) {

        Map<Long, Double> predictedScores = new HashMap<>();
        Map<Long, Double> similaritySums = new HashMap<>();

        for (Long similarUserId : topKUserIds) {
            double similarity = similarityMap.get(similarUserId);
            List<MovieCreatedAtResponse> others = userRatingsMap.get(similarUserId);

            for (MovieCreatedAtResponse dto : others) {
                Long movieId = dto.getMovieId();
                if (targetRatingMap.containsKey(movieId)) continue;

                double timeWeight = 1.0 / Math.sqrt(Duration.between(dto.getCreatedAt(), LocalDateTime.now()).toDays() + 1);
                double weightedScore = dto.getScore() * similarity * timeWeight;

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

    // 유저 평점을 바탕으로 코사인 유사도 계산
    private double cosineSimilarity(Map<Long, Double> a, Map<Long, Double> b) {
        Set<Long> common = new HashSet<>(a.keySet());
        common.retainAll(b.keySet());
        if (common.isEmpty()) return 0.0;

        double dot = 0, normA = 0, normB = 0;
        for (Long key : common) dot += a.get(key) * b.get(key);
        for (double v : a.values()) normA += v * v;
        for (double v : b.values()) normB += v * v;

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}