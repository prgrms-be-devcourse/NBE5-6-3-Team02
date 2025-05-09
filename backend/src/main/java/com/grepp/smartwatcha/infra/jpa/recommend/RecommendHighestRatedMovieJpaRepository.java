package com.grepp.smartwatcha.infra.jpa.recommend;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecommendHighestRatedMovieJpaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> findTop10ByAverageRating() {
        return entityManager.createQuery(
                        "SELECT r.movie.id, AVG(r.score) as avgScore " +
                                "FROM RatingEntity r " +
                                "GROUP BY r.movie.id " +
                                "ORDER BY avgScore DESC", Object[].class)
                .setMaxResults(10)
                .getResultList();
    }
}
