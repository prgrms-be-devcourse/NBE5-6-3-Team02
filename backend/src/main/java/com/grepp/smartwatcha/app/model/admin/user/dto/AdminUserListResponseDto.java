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
public class AdminUserListResponseDto {
  private Long id;
  private String name;
  private String email;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  private Boolean activated;
  private String role;
  private boolean updatedRecently;
  private List<AdminSimpleRatingDto> recentRatings;
}
