package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j;

import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/*
 * 공개 예정작 영화 장르 병합 헬퍼
 * 기존 장르 노드와 새로운 장르 ID 목록을 병합하여 중복 없이 통합
 * 
 * 주요 기능:
 * - 기존 장르 노드와 새로운 장르 ID 목록 비교
 * - 중복되지 않은 새로운 장르만 추가
 * - 장르 ID를 장르 이름으로 변환
 */
@Component
@RequiredArgsConstructor
public class UpcomingMovieGenreMergeHelper {

  private final UpcomingMovieGenreFetchNeo4jService genreFetchService;

  // 기존 장르 노드와 새로운 장르 ID 목록을 병합
  // 중복되는 장르는 제외하고 새로운 장르만 추가
  public List<GenreNode> mergeGenres(List<GenreNode> existing, List<Long> incomingGenreIds) {
    Map<Long, String> genreMap = genreFetchService.getGenreMap();

    Set<String> incomingNames = incomingGenreIds.stream()
        .map(genreMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Set<String> existingNames = existing.stream()
        .map(GenreNode::getName)
        .collect(Collectors.toSet());

    incomingNames.removeAll(existingNames);

    List<GenreNode> merged = new ArrayList<>(existing);
    for (String name : incomingNames) {
      merged.add(new GenreNode(name));
    }

    return merged;
  }
}
