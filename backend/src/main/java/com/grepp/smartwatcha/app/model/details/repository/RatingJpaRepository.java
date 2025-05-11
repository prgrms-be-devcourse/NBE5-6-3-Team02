package com.grepp.smartwatcha.app.model.details.repository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingJpaRepository extends JpaRepository<RatingEntity, Long> {

}
