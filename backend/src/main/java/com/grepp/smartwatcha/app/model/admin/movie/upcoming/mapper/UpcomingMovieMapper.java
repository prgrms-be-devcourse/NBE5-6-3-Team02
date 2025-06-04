package com.grepp.smartwatcha.app.model.admin.movie.upcoming.mapper;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j.UpcomingMovieGenreFetchNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.neo4j.node.ActorNode;
import com.grepp.smartwatcha.infra.neo4j.node.DirectorNode;
import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import com.grepp.smartwatcha.infra.neo4j.node.WriterNode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
 * 공개 예정작 영화 정보 매퍼
 * DTO 와 엔티티 간의 변환을 담당
 *
 * 주요 기능:
 * - DTO 를 JPA 엔티티로 변환
 * - DTO 를 Neo4j 노드로 변환
 * - 날짜 문자열을 LocalDateTime 으로 변환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpcomingMovieMapper {

  private final UpcomingMovieGenreFetchNeo4jService genreFetchService;

  /*
   * DTO 를 JPA 엔티티로 변환
   *
   * 변환 과정:
   * 1. 개봉일 문자열을 LocalDateTime 으로 변환
   * 2. overview 가 비어있는 경우 null 처리
   * 3. 개봉 여부 판단 (현재 날짜 기준)
   */
  public MovieEntity toJpaEntity(UpcomingMovieDto dto) {
    LocalDateTime releaseDateTime = parseDateWithDefaultTime(dto.getReleaseDate(), dto.getTitle());

    return MovieEntity.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        .poster(dto.getPosterPath())
        .releaseDate(releaseDateTime)
        .overview(
            dto.getOverview() == null || dto.getOverview().isBlank() ? null : dto.getOverview())
        .country(dto.getCountry())
        .isReleased(
            releaseDateTime != null && releaseDateTime.toLocalDate().isBefore(LocalDate.now()))
        .certification(dto.getCertification())
        .build();
  }

  /*
   * 날짜 문자열을 LocalDateTime  으로 변환
   *
   * 변환 과정:
   * 1. 날짜 문자열이 비어있는지 확인
   * 2. LocalDate 로 파싱 후 00:00 시간 설정
   */
  public LocalDateTime parseDateWithDefaultTime(String dateStr, String title) {
    if (dateStr == null || dateStr.isBlank()) {
      log.warn("📅 releaseDate 가 비어 있어 LocalDateTime 으로 변환할 수 없습니다. [title: {}]", title);
      return null;
    }

    try {
      return LocalDate.parse(dateStr).atStartOfDay(); // 00:00
    } catch (Exception e) {
      log.warn("📅 releaseDate 파싱 실패: '{}' [title: {}]", dateStr, title);
      return null;
    }
  }

  // DTO 를 Neo4j 노드로 변환
  public MovieNode toNeo4jNode(
      UpcomingMovieDto dto,
      List<ActorNode> actors,
      List<DirectorNode> directors,
      List<WriterNode> writers,
      List<GenreNode> genres
  ) {
    MovieNode movie = new MovieNode(dto.getId(), dto.getTitle());
    movie.setActors(new ArrayList<>(actors));
    movie.setDirectors(new ArrayList<>(directors));
    movie.setWriters(new ArrayList<>(writers));
    movie.setGenres(new ArrayList<>(genres));
    return movie;
  }
}
