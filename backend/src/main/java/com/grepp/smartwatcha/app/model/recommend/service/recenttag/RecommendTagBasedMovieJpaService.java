package com.grepp.smartwatcha.app.model.recommend.service.recenttag;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.RatingRecommendJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Transactional("jpaTransactionManager")
@Service
@RequiredArgsConstructor
public class RecommendTagBasedMovieJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private final MovieQueryJpaRepository movieRepository;

    public Long findMostRecentRatedMovieId(Long userId) {
        return ratingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(r -> r.getMovie().getId())
                .orElse(null);
    }

    public List<MovieEntity> findMoviesNotRatedByUser(Long userId) {
        return movieRepository.findMoviesNotRatedByUser(userId);
    }

    public Map<Long, Double> calculateAverageScores(List<MovieEntity> movies) {
        List<Long> movieIds = movies.stream().map(MovieEntity::getId).toList();
        List<Object[]> results = movieRepository.findAverageScoresByMovieIds(movieIds);

        Map<Long, Double> scoreMap = new HashMap<>();
        for (Object[] row : results) {
            Long movieId = (Long) row[0];
            Double avg = (Double) row[1];
            scoreMap.put(movieId, avg);
        }
        return scoreMap;
    }

    public MovieEntity findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Movie not found with id: " + id));
    }
}