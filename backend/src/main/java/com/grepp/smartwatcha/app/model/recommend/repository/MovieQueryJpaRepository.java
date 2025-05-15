package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MovieQueryJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public MovieEntity findById(Long id) {
        return em.find(MovieEntity.class, id);
    }

    public List<MovieEntity> findAllReleased() {
        return em.createQuery(
                        "SELECT m FROM MovieEntity m WHERE m.isReleased = true", MovieEntity.class)
                .getResultList();
    }
}
