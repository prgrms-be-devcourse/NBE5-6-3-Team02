package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j.UpcomingMovieSaveNeo4jService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSaveJpaService;
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
      jpaService.saveToJpa(dto);
      neo4jService.saveToNeo4j(dto);
    } catch (Exception e) {
      log.error("Neo4j 저장 실패 → JPA 롤백은 되지 않음. 수동 삭제 고려");
    }
  }
}
