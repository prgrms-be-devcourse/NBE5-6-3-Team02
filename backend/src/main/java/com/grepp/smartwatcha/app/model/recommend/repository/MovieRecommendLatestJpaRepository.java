package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MovieRecommendLatestJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<MovieEntity> findTop10ByOrderByCreatedAtDesc() {
        return em.createQuery(
                        "SELECT m FROM MovieEntity m ORDER BY m.createdAt DESC",
                        MovieEntity.class)
                .setMaxResults(10)
                .getResultList();
    }
}

