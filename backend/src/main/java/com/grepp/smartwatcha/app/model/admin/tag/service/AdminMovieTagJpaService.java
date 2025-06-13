package com.grepp.smartwatcha.app.model.admin.tag.service;

import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageDto;
import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageWithUsersDto;
import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUserUsageDto;
import com.grepp.smartwatcha.app.model.admin.tag.repository.AdminMovieTagJpaRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMovieTagJpaService { // 관리자 페이지에서 태그 관련 통계를 제공하는 서비스 클래스

  private final AdminMovieTagJpaRepository adminMovieTagJpaRepository;

  // 전체 태그별 사용 횟수를 집계하여 Map 으로 반환
  // @return 태그 ID를 키로 하고 사용 횟수를 값으로 가지는 Map
  public Map<Long, Long> getTagUsageCountMap(){
    List<AdminTagUsageDto> usageStats = adminMovieTagJpaRepository.findAllTagUsageStats();
    return usageStats.stream()
        .collect(Collectors.toMap(
            AdminTagUsageDto::getTagId,
            AdminTagUsageDto::getCount,
            Long::sum // 동일 태그 ID가 있을 경우 사용 횟수를 합산
        ));
  }

  // 전체 태그에 대해 유저별 사용 상세 정보를 포함한 통계를 반환
  // @return 태그 ID를 키로 하고 상세 정보를 담은 DTO 를 값으로 가지는 Map
  public Map<Long, AdminTagUsageWithUsersDto> getTagUsageWithUserDetailMap() {
    List<AdminTagUsageDto> usageStats = adminMovieTagJpaRepository.findAllTagUsageStats();

    Map<Long, AdminTagUsageWithUsersDto> result = new HashMap<>();

    // 각 유저 DTO 에 태그 ID 설정
    for(AdminTagUsageDto stat : usageStats) {
      List<AdminTagUserUsageDto> userDetails = adminMovieTagJpaRepository.findUserUsageByTagId(stat.getTagId());

      for(AdminTagUserUsageDto dto : userDetails) {
        dto.setTagId(stat.getTagId());
      }

      AdminTagUsageWithUsersDto dto = new AdminTagUsageWithUsersDto(
          stat.getTagId(),
          stat.getTagName(),
          stat.getCount(),
          userDetails
      );
      result.put(stat.getTagId(), dto);
    }
    return result;
  }

  // 여러 영화에 대해 각 영화별 태그 사용 통계를 반환
  // @param movieIds 영화 ID 리스트
  // * @return 영화 ID를 키로 하고 태그 사용 통계 리스트를 값으로 가지는 Map
  public Map<Long, List<AdminTagUsageDto>> getTagStatsMapGroupedByMovieIds(List<Long> movieIds) {
    List<AdminTagUsageDto> allStats = adminMovieTagJpaRepository.findTagStatsByMovieIds(movieIds);
    return allStats.stream().collect(Collectors.groupingBy(AdminTagUsageDto::getMovieId));
  }
}
