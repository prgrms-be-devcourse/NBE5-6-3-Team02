package com.grepp.smartwatcha.app.model.admin.tag.service;

import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageDto;
import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageWithUsersDto;
import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUserUsageDto;
import com.grepp.smartwatcha.app.model.admin.tag.mapper.AdminMovieTagMapper;
import com.grepp.smartwatcha.app.model.admin.tag.repository.AdminMovieTagJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMovieTagJpaService {

  private final AdminMovieTagJpaRepository adminMovieTagJpaRepository;

  // 전체 태그별 사용 횟수 집계
  public Map<Long, Long> getTagUsageCountMap() {
    List<MovieTagEntity> entities = adminMovieTagJpaRepository.findAllForTagStats();
    return entities.stream()
        .collect(Collectors.groupingBy(
            mt -> mt.getTag().getId(),
            Collectors.counting()
        ));
  }

  // 태그별 유저 상세 사용 통계 반환
  public Map<Long, AdminTagUsageWithUsersDto> getTagUsageWithUserDetailMap() {
    List<MovieTagEntity> entities = adminMovieTagJpaRepository.findAllForTagStats();

    Map<Long, List<MovieTagEntity>> groupedByTag = entities.stream()
        .collect(Collectors.groupingBy(mt -> mt.getTag().getId()));

    Map<Long, AdminTagUsageWithUsersDto> result = new HashMap<>();

    for (Map.Entry<Long, List<MovieTagEntity>> entry : groupedByTag.entrySet()) {
      Long tagId = entry.getKey();
      List<MovieTagEntity> tagEntities = entry.getValue();

      String tagName = tagEntities.get(0).getTag().getName();
      Long usageCount = (long) tagEntities.size();

      List<AdminTagUserUsageDto> userDtos = tagEntities.stream()
          .map(AdminMovieTagMapper::toUserUsageDto)
          .collect(Collectors.toList());

      result.put(tagId, AdminMovieTagMapper.toUsageWithUsersDto(
          tagId, tagName, usageCount, userDtos
      ));
    }
    return result;
  }

  // 영화 ID별 태그 통계 반환
  public Map<Long, List<AdminTagUsageDto>> getTagStatsMapGroupedByMovieIds(List<Long> movieIds) {
    List<MovieTagEntity> entities = adminMovieTagJpaRepository.findAllByMovieIds(movieIds);

    // 영화ID + 태그ID 기준 카운트
    Map<String, Long> countMap = entities.stream()
        .collect(Collectors.groupingBy(
            mt -> mt.getMovie().getId() + ":" + mt.getTag().getId(),
            Collectors.counting()
        ));

    Map<Long, List<AdminTagUsageDto>> result = new HashMap<>();
    for (MovieTagEntity mt : entities) {
      Long movieId = mt.getMovie().getId();
      String key = movieId + ":" + mt.getTag().getId();
      Long count = countMap.getOrDefault(key, 0L);

      AdminTagUsageDto dto = AdminMovieTagMapper.toUsageDto(mt, count);
      result.computeIfAbsent(movieId, k -> new ArrayList<>()).add(dto);
    }

    return result;
  }
}
