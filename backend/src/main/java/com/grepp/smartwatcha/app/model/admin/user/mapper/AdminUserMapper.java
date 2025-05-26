package com.grepp.smartwatcha.app.model.admin.user.mapper;

import com.grepp.smartwatcha.app.model.admin.user.dto.AdminSimpleRatingDto;
import com.grepp.smartwatcha.app.model.admin.user.dto.AdminUserListResponseDto;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import java.time.LocalDateTime;
import java.util.List;

// UserEntity 와 해당 유저의 최근 평가 리스트를 받아, 관리자 유저 리스트 화면에 사용할 AdminUserListResponseDto 로 변환
// 변환 로직:
  // - 유저 기본 정보(id, name, email 등) 복사
  // - 최근 평가 정보(recentRatings)를 포함
  // - 수정일이 3일 이내면 updatedRecently = true 로 설정
public class AdminUserMapper {

  public static AdminUserListResponseDto toDto(UserEntity user, List<AdminSimpleRatingDto> recentRatings) {
    LocalDateTime now = LocalDateTime.now();
    boolean updatedRecently = user.getModifiedAt() != null &&
        user.getModifiedAt().isAfter(now.minusDays(3));

    AdminUserListResponseDto dto = AdminUserListResponseDto.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .phoneNumber(user.getPhoneNumber())
        .createdAt(user.getCreatedAt())
        .modifiedAt(user.getModifiedAt())
        .activated(user.getActivated())
        .role(user.getRole().name())
        .recentRatings(recentRatings)
        .updatedRecently(updatedRecently)
        .build();

    return dto;
  }
}
