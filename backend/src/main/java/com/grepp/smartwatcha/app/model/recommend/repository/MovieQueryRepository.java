package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MovieQueryRepository {

    @PersistenceContext
    private EntityManager em;

    public MovieEntity findById(Long id) {
        return em.find(MovieEntity.class, id);
    }
}
