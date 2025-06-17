package com.grepp.smartwatcha.app.model.recommend.service.recenttag;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.RatingRecommendJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Transactional("jpaTransactionManager")
@Service
@RequiredArgsConstructor
public class RecommendTagBasedMovieJpaService {

    private final RatingRecommendJpaRepository ratingRepository;
    private final MovieQueryJpaRepository movieRepository;

    public Long findMostRecentRatedMovieId(Long userId) {
        Optional<RatingEntity> recentRating = ratingRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
        return recentRating.map(r -> r.getMovie().getId()).orElse(null);
    }

    public List<Long> findUnratedMovieIdsByUser(Long userId) {
        List<MovieEntity> movies = movieRepository.findMoviesNotRatedByUser(userId);
        List<Long> ids = new ArrayList<>();
        for (MovieEntity movie : movies) {
            ids.add(movie.getId());
        }
        return ids;
    }

    public Map<Long, Double> findTopRatedMovieScoresByIds(List<Long> movieIds) {
        List<Object[]> rows = movieRepository.findAverageScoresByMovieIds(movieIds);
        rows.sort((a, b) -> Double.compare((Double) b[1], (Double) a[1]));
        List<Object[]> top10 = rows.subList(0, Math.min(10, rows.size()));

        Map<Long, Double> result = new LinkedHashMap<>();
        for (Object[] row : top10) {
            result.put((Long) row[0], (Double) row[1]);
        }
        return result;
    }

    public List<MovieEntity> findMoviesByIds(List<Long> ids) {
        return movieRepository.findByIdIn(ids);
    }
}