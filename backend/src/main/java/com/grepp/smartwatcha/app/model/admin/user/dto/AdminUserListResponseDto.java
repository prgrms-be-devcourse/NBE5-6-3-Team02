package com.grepp.smartwatcha.app.model.admin.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
// Admin User List 페이지에서 사용되는 유저 정보 응답 DTO
// 사용 위치 : /admin/users
public class AdminUserListResponseDto {
  private Long id;
  private String name;
  private String email;
  private String phoneNumber;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  private Boolean activated;
  private String role;
  private boolean updatedRecently; // 최근 정보 수정 여부(3일 동안 update 표시)
  private List<AdminSimpleRatingDto> recentRatings; // 유저가 평가한 최근 기록 표시
}
