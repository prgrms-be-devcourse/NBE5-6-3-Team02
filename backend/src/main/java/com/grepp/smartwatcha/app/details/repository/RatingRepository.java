package com.grepp.smartwatcha.app.details.repository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findAllByUser_Id(Long id);
}
