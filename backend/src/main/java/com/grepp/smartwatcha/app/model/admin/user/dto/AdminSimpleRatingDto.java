package com.grepp.smartwatcha.app.model.admin.user.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Admin User List 페이지 유저 상세 모달에 보여질 간단한 평가 정보 DTO
// 사용 위치: /admin/users (회원 목록 페이지)
// 각 유저의 상세 모달에서 최근 평가 최대 8건 등 간략히 보여줄 때 사용
public class AdminSimpleRatingDto {
  private String title;
  private Double score;
  private LocalDateTime createdAt;
}
