package com.grepp.smartwatcha.app.model.recommend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecommendHighestRatedMovieJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Object[]> findTop10ByAverageRating() {
        return em.createQuery(
                        "SELECT r.movie.id, AVG(r.score) as avgScore " +
                                "FROM RatingEntity r " +
                                "GROUP BY r.movie.id " +
                                "ORDER BY avgScore DESC", Object[].class)
                .setMaxResults(10)
                .getResultList();
    }
}
