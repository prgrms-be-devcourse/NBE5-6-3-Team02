package com.grepp.smartwatcha.app.model.admin.tag.mapper;

import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageDto;
import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageWithUsersDto;
import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUserUsageDto;
import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import java.util.List;

// 관리자 태그 통계 전용 Mapper 클래스
public class AdminMovieTagMapper {

  // MovieTagEntity → AdminTagUserUsageDto 변환
  // (유저가 어떤 영화에서 특정 태그를 사용했는지)
  public static AdminTagUserUsageDto toUserUsageDto(MovieTagEntity mt){
    return AdminTagUserUsageDto.builder()
        .tagId(mt.getTag().getId())
        .userId(mt.getUser().getId())
        .userName(mt.getUser().getName())
        .movieId(mt.getMovie().getId())
        .movieTitle(mt.getMovie().getTitle())
        .build();
  }

  // MovieTagEntity + 사용 횟수 → AdminTagUsageDto 변환
  // (특정 영화에서 태그가 몇 번 사용되었는지)
  public static AdminTagUsageDto toUsageDto(MovieTagEntity mt, Long count) {
    return AdminTagUsageDto.builder()
        .tagId(mt.getTag().getId())
        .movieId(mt.getMovie().getId())
        .tagName(mt.getTag().getName())
        .count(count)
        .build();
  }

  // 태그 ID/이름/전체 사용 횟수 + 유저 사용 내역 → AdminTagUsageWithUsersDto 생성
  // (해당 태그의 총 사용 내역 및 유저별 상세 정보 포함)
  public static AdminTagUsageWithUsersDto toUsageWithUsersDto(
      Long tagId, String tagName, Long usageCount, List<AdminTagUserUsageDto> userUsages
  ) {
    return AdminTagUsageWithUsersDto.builder()
        .tagId(tagId)
        .tagName(tagName)
        .usageCount(usageCount)
        .userUsages(userUsages)
        .build();
  }
}
