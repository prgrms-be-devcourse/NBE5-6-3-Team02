package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRecommendJpaRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findByUserId(Long userId);
}
