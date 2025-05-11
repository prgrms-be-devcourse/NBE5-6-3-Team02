package com.grepp.smartwatcha.app.model.recommend.service;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieRecommendLatestJpaRepository;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendLatestResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendLatestRatedJpaService {

    private final MovieRecommendLatestJpaRepository movieRepo;
    private final RecommendLatestRatedNeo4jService genreService;

    @PersistenceContext
    private final EntityManager em;

    @Transactional("jpaTransactionManager")
    public List<MovieRecommendLatestResponse> getTop10LatestMoviesWithGenres() {
        return movieRepo.findTop10ByOrderByCreatedAtDesc().stream()
                .map(movie -> {
                    Double avgScore = getAverageScore(movie.getId());
                    List<String> genres = genreService.getGenresByMovieId(movie.getId());
                    return MovieRecommendLatestResponse.from(movie, avgScore, genres);
                })
                .toList();
    }

    public Double getAverageScore(Long movieId) {
        Double result = em.createQuery(
                        "SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.id = :movieId",
                        Double.class)
                .setParameter("movieId", movieId)
                .getSingleResult();
        return result != null ? result : 0.0;
    }
}

