package com.grepp.smartwatcha.app.model.admin.user;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager", readOnly = true)
public class AdminUserRatingJpaService {

  private final AdminUserRatingJpaRepository ratingRepository;

  public Page<AdminRatingDto> getRatings(Long userId, Pageable pageable) {

    Page<RatingEntity> ratingPage;

    if (userId != null) {
      ratingPage = ratingRepository.findAllByUserId(userId, pageable);
    } else {
      ratingPage = ratingRepository.findAll(pageable);
    }

    return ratingPage.map((RatingEntity rating) -> AdminRatingDto.builder()
        .title(rating.getMovie().getTitle())
        .score(rating.getScore())
        .createdAt(rating.getCreatedAt())
        .userName(rating.getUser().getName())
        .build());
  }
}
