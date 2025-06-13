package com.grepp.smartwatcha.app.model.admin.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTagUsageDto { // 태그 사용 통계를 나타내는 DTO
  private Long tagId;
  private Long movieId;
  private String tagName;
  private Long count;
}
