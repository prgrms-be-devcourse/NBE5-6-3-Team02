package com.grepp.smartwatcha.app.model.admin.user;

import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;

public class AdminUserMapper {
  public static AdminUserListResponse toDto(UserEntity user, List<AdminSimpleRatingDto> recentRatings) {
    AdminUserListResponse dto = AdminUserListResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .createdAt(user.getCreatedAt())
        .modifiedAt(user.getModifiedAt())
        .activated(user.getActivated())
        .role(user.getRole().name())
        .recentRatings(recentRatings)
        .build();

    boolean isRecentlyModified = user.getModifiedAt() != null && user.getModifiedAt().isAfter(LocalDateTime.now().minusDays(3));
    dto.setUpdatedRecently(isRecentlyModified);

    return dto;
  }
}
