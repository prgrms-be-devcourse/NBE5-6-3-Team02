package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSaveJpaService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j.UpcomingMovieSaveNeo4jService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieUnifiedSaveService {

  private final UpcomingMovieSaveJpaService jpaService;
  private final UpcomingMovieSaveNeo4jService neo4jService;

  public void saveAll(UpcomingMovieDto dto) {
    try {
      jpaService.saveToJpa(dto); // MySQL 저장
    } catch (Exception e) {
      log.error("❌ [JPA 저장 실패] 영화 ID: {}, 제목: {}", dto.getId(), dto.getTitle(), e);
      throw new RuntimeException("JPA 저장 실패", e);
    }

    try {
      neo4jService.saveToNeo4j(dto); // Neo4j 저장
    } catch (Exception e) {
      log.error("❌ [Neo4j 저장 실패] 영화 ID: {}, 제목: {}", dto.getId(), dto.getTitle());
      log.error("❗ 예외 클래스: {}, 메시지: {}", e.getClass().getName(), e.getMessage());
      log.error("🔍 StackTrace ↓↓↓", e);

      // JPA 수동 롤백 (저장한 영화 ID 삭제)
      try {
        jpaService.deleteFromJpaById(dto.getId());
        log.warn("🧹 JPA 저장 내용 수동 롤백 완료: ID={}", dto.getId());
      } catch (Exception rollbackEx) {
        log.error("❗ JPA 수동 롤백 중 예외 발생", rollbackEx);
      }

      throw new RuntimeException("Neo4j 저장 실패로 인한 JPA 롤백 처리 완료", e);
    }
  }
}
