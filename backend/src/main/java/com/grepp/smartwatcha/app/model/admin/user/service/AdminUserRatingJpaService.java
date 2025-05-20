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
public class AdminUserRatingJpaService {

  private final AdminUserRatingJpaRepository ratingRepository;

  public Page<AdminRatingDto> getRatings(Long userId, Pageable pageable) {
    Page<RatingEntity> ratingPage = (userId != null)
        ? ratingRepository.findAllByUserId(userId, pageable)
        : ratingRepository.findAll(pageable);

    return ratingPage.map(this::toDto);
  }

  private AdminRatingDto toDto(RatingEntity rating) {
    return AdminRatingDto.builder()
        .title(rating.getMovie().getTitle())
        .score(rating.getScore())
        .createdAt(rating.getCreatedAt())
        .userName(rating.getUser().getName())
        .build();
  }
}
