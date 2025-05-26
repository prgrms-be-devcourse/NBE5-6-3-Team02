package com.grepp.smartwatcha.app.model.admin.user.repository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// Admin User Ratings 페이지에서 유저 평가(Rating) 관련 기능을 위한 JPA 레포지토리
public interface AdminUserRatingJpaRepository extends JpaRepository<RatingEntity, Long> {
  // 특정 유저의 최근 평가 8개 조회 (내림차순 정렬) - 유저 상세 모달에 간단한 평가 리스트 보여줄 때 사용
  List<RatingEntity> findTop8ByUserIdOrderByCreatedAtDesc(Long Id);
  // 특정 유저의 전체 평가를 페이징으로 조회
  Page<RatingEntity> findAllByUserId(Long id, Pageable pageable);
  // 전체 유저의 모든 평가를 페이징으로 조회
  Page<RatingEntity> findAll(Pageable pageable);
}
