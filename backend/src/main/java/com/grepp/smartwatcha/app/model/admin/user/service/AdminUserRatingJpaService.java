package com.grepp.smartwatcha.app.model.admin.user.service;

import com.grepp.smartwatcha.app.model.admin.user.dto.AdminRatingDto;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserRatingJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager", readOnly = true)
// Admin User Ratings 페이지에서 유저의 평가(Rating) 정보를 조회하기 위한 서비스 클래스
public class AdminUserRatingJpaService {

  private final AdminUserRatingJpaRepository ratingRepository;

  // 특정 유저의 평가 목록을 조회하거나, userId가 없을 경우 전체 유저의 평가 목록을 조회
  public Page<AdminRatingDto> getRatings(Long userId, Pageable pageable) {
    Page<RatingEntity> ratingPage = (userId != null)
        ? ratingRepository.findAllByUserId(userId, pageable)
        : ratingRepository.findAll(pageable);

    return ratingPage.map(this::toDto);
  }

  // RatingEntity 를 AdminRatingDto 로 변환
  private AdminRatingDto toDto(RatingEntity rating) {
    return AdminRatingDto.builder()
        .title(rating.getMovie().getTitle())
        .score(rating.getScore())
        .createdAt(rating.getCreatedAt())
        .userName(rating.getUser().getName())
        .build();
  }
}
