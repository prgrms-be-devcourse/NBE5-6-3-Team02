package com.grepp.smartwatcha.app.model.admin.user.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Admin User Ratings 페이지 보여줄 때 사용되는 DTO
// 사용 위치: /admin/users/ratings (유저 평가 전체 조회 페이지)
public class AdminRatingDto {
  private String title;
  private Double score;
  private LocalDateTime createdAt;
  private String userName;
}
