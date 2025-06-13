package com.grepp.smartwatcha.app.model.admin.tag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class AdminTagUserUsageDto { // 태그 사용 유저의 영화별 사용 내역을 나타내는 DTO.
  private Long tagId;
  private Long userId;
  private String userName;
  private Long movieId;
  private String movieTitle;

  public AdminTagUserUsageDto(Long tagId, Long userId, String userName, Long movieId, String movieTitle) {
    this.tagId = tagId;
    this.userId = userId;
    this.userName = userName;
    this.movieId = movieId;
    this.movieTitle = movieTitle;
  }
}
