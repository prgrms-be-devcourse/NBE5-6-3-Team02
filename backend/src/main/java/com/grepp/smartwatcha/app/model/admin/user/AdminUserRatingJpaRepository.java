package com.grepp.smartwatcha.app.model.admin.user;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRatingJpaRepository extends JpaRepository<RatingEntity, Long> {
  List<RatingEntity> findTop8ByUserIdOrderByCreatedAtDesc(Long Id);
  Page<RatingEntity> findAllByUserId(Long id, Pageable pageable);
  Page<RatingEntity> findAll(Pageable pageable);
}
