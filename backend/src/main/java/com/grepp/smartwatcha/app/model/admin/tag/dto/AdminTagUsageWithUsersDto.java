package com.grepp.smartwatcha.app.model.admin.tag.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTagUsageWithUsersDto { // 태그별 유저 상세 사용 내역을 포함한 통계 DTO
  private Long tagId;
  private String tagName;
  private Long usageCount; // 태그 전체 사용 횟수
  private List<AdminTagUserUsageDto> userUsages; // 해당 태그를 사용한 유저들의 상세 정보 리스트
}
