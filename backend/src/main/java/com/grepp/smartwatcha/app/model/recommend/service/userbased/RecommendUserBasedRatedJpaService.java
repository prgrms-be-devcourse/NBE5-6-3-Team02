package com.grepp.smartwatcha.app.model.recommend.service.userbased;

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

    // 유저와 유사한 사용자의 별점 바탕으로 영화별 예측 점수 계산
    public Map<Long, Double> calculateUserBasedScores(Long userId) {
        List<RatingEntity> myRatings = ratingRepository.findByUserId(userId);
        if (myRatings.isEmpty()) return Map.of();

        Map<Long, Double> targetRatingMap = buildRatingMap(myRatings);
        List<Long> targetMovieIds = extractMovieIds(myRatings);

        List<Long> candidateUserIds = ratingRepository.findUsersWithCommonRatedMovies(userId, targetMovieIds);
        if (candidateUserIds.isEmpty()) return Map.of();

        List<Long> userIdList = new ArrayList<>(candidateUserIds);
        userIdList.add(userId);

        Map<Long, List<RatingEntity>> userRatingsMap = groupRatingsByUser(ratingRepository.findByUserIdIn(userIdList));
        Map<Long, Double> similarityMap = calculateSimilarities(candidateUserIds, targetRatingMap, userRatingsMap);

        List<Long> topKUserIds = getTopKSimilarUsers(similarityMap);

        return predictScores(topKUserIds, targetRatingMap, userRatingsMap, similarityMap);
    }

    // 별점 리스트를 id 기준으로 map으로 변환
    private Map<Long, Double> buildRatingMap(List<RatingEntity> ratings) {
        Map<Long, Double> ratingMap = new HashMap<>();
        for (RatingEntity rating : ratings) {
            ratingMap.put(rating.getMovie().getId(), rating.getScore());
        }
        return ratingMap;
    }

    // 영화 id만 리스트로 반환
    private List<Long> extractMovieIds(List<RatingEntity> ratings) {
        List<Long> ids = new ArrayList<>();
        for (RatingEntity rating : ratings) {
            ids.add(rating.getMovie().getId());
        }
        return ids;
    }

    // 리스트들 그룹핑하여 map으로 반환
    private Map<Long, List<RatingEntity>> groupRatingsByUser(List<RatingEntity> ratings) {
        Map<Long, List<RatingEntity>> userRatingsMap = new HashMap<>();
        for (RatingEntity rating : ratings) {
            userRatingsMap.computeIfAbsent(rating.getUser().getId(), k -> new ArrayList<>()).add(rating);
        }
        return userRatingsMap;
    }

    // 유사 사용자 후보와 유사도 계산하여 map으로 반환
    private Map<Long, Double> calculateSimilarities(
            List<Long> candidateUserIds,
            Map<Long, Double> targetRatingMap,
            Map<Long, List<RatingEntity>> userRatingsMap) {

        Map<Long, Double> similarityMap = new HashMap<>();

        for (Long otherUserId : candidateUserIds) {
            List<RatingEntity> otherRatings = userRatingsMap.get(otherUserId);
            if (otherRatings == null) continue;

            Map<Long, Double> otherRatingMap = buildRatingMap(otherRatings);
            double similarity = cosineSimilarity(targetRatingMap, otherRatingMap);
            if (similarity > 0) {
                similarityMap.put(otherUserId, similarity);
            }
        }

        return similarityMap;
    }

    // 상위 k(10)명의 유저 id리스트 반환
    private List<Long> getTopKSimilarUsers(Map<Long, Double> similarityMap) {
        List<Map.Entry<Long, Double>> sorted = new ArrayList<>(similarityMap.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Long> topK = new ArrayList<>();
        for (int i = 0; i < Math.min(K, sorted.size()); i++) {
            topK.add(sorted.get(i).getKey());
        }
        return topK;
    }

    // 별점과 유사도 이용하여 예측 평점 계산 후 반환
    private Map<Long, Double> predictScores(
            List<Long> topKUserIds,
            Map<Long, Double> targetRatingMap,
            Map<Long, List<RatingEntity>> userRatingsMap,
            Map<Long, Double> similarityMap) {

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

    // 두 유저의 영화 별점 간의 유사도 계산
    private double cosineSimilarity(Map<Long, Double> a, Map<Long, Double> b) {
        Set<Long> common = new HashSet<>(a.keySet());
        common.retainAll(b.keySet());
        if (common.isEmpty()) return 0.0;

        double dotProduct = 0, normA = 0, normB = 0;
        for (Long key : common) dotProduct += a.get(key) * b.get(key);
        for (double v : a.values()) normA += v * v;
        for (double v : b.values()) normB += v * v;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}