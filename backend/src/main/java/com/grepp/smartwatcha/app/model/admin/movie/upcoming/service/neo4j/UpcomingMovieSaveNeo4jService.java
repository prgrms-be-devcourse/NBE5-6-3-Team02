package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 공개 예정작 영화 정보를 Neo4j에 저장하는 서비스 클래스
// 한 번의 Cypher 쿼리로 영화 노드 및 관계를 MERGE(upsert) 방식으로 처리
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "neo4jTransactionManager")
public class UpcomingMovieSaveNeo4jService {

  private final Neo4jClient neo4jClient;     // Neo4j 클라이언트

  // UpcomingMovieDto 정보를 기반으로 Neo4j 저장을 위한 파라미터 구성
  // - 이후 Cypher 쿼리 또는 Custom Repository에 전달하여 노드 및 관계 저장에 활용됨
  public void saveToNeo4j(UpcomingMovieDto dto) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", dto.getId());
    params.put("title", dto.getTitle());
    params.put("actorNames", dto.getActorNames());
    params.put("directorNames", dto.getDirectorNames());
    params.put("writerNames", dto.getWriterNames());
    params.put("genreIds", dto.getGenreIds());

    // 2. Cypher 쿼리: 영화 노드 MERGE, 기존 관계 삭제, 관계 재생성
    String cypher = """
        MERGE (m:MOVIE {id: $id})
          SET m.title = $title
        WITH m
        // 기존 관계 전부 삭제
        OPTIONAL MATCH (m)-[old:ACTED_IN|DIRECTED_BY|WRITTEN_BY|HAS_GENRE]->()
        DELETE old
        WITH m
        // ACTED_IN 재생성
        UNWIND $actorNames AS actorName
          MERGE (a:ACTOR {name: actorName})
          MERGE (m)-[:ACTED_IN]->(a)
        WITH m
        // DIRECTED_BY 재생성
        UNWIND $directorNames AS directorName
          MERGE (d:DIRECTOR {name: directorName})
          MERGE (m)-[:DIRECTED_BY]->(d)
        WITH m
        // WRITTEN_BY 재생성
        UNWIND $writerNames AS writerName
          MERGE (w:WRITER {name: writerName})
          MERGE (m)-[:WRITTEN_BY]->(w)
        WITH m
        // HAS_GENRE 재생성
        UNWIND $genreIds AS genreId
          MERGE (g:GENRE {id: genreId})
          MERGE (m)-[:HAS_GENRE]->(g)
        """;

    // 3. 실행
    neo4jClient.query(cypher)
        .bindAll(params)
        .run();

    log.info("✅ [saveToNeo4j] 저장 완료: {} (ID={})", dto.getTitle(), dto.getId());
  }

  // enrich된 공개 예정작 DTO 리스트를 기반으로
  // Neo4j에 각 영화 및 관련 노드/관계를 저장하는 메서드
  public void saveFromDtos(List<UpcomingMovieDto> dtoList) {
    for (UpcomingMovieDto dto : dtoList) {
      saveToNeo4j(dto);
    }
  }
}
