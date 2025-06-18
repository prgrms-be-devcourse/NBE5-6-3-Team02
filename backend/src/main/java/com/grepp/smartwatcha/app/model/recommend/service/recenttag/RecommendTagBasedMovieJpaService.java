package com.grepp.smartwatcha.app.model.recommend.service.recenttag;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieTagRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.RatingRecommendJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "jpaTransactionManager", readOnly = true)
public class RecommendTagBasedMovieJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private final MovieQueryJpaRepository movieQueryRepository;
    private final MovieTagRepository movieTagRepository;

    // 가장 최근에 평가한 영화 id 반환
    public Long findMostRecentRatedMovieId(Long userId) {
        return ratingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(r -> r.getMovie().getId())
                .orElse(null);
    }

    // 평가한 영화들의 id 목록 반환
    public List<Long> findRatedMovieIdsByUser(Long userId) {
        return ratingRepository.findByUserId(userId).stream()
                .map(r -> r.getMovie().getId())
                .distinct()
                .toList();
    }

    // 공개된 모든 영화 조회
    public List<MovieEntity> findAllReleasedMovies() {
        return movieQueryRepository.findAllReleased();
    }

    // 태그한 태그 이름 리스트 조회
    public List<String> findTagsOfMovieByUser(Long userId, Long movieId) {
        List<Object[]> rows = movieTagRepository.findTagNamesByUserAndMovieIds(userId, List.of(movieId));
        return rows.stream().map(r -> (String) r[1]).toList();
    }

    // 각 영화의 평균 별점 반환
    public Map<Long, Double> getAverageScoreMap(List<Long> movieIds) {
        return movieQueryRepository.findAverageScoresByMovieIds(movieIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Double) row[1]
                ));
    }
}