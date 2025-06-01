package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.mapper.UpcomingMovieMapper;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.neo4j.UpcomingMovieNeo4jRepository;
import com.grepp.smartwatcha.infra.neo4j.node.ActorNode;
import com.grepp.smartwatcha.infra.neo4j.node.DirectorNode;
import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import com.grepp.smartwatcha.infra.neo4j.node.WriterNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/*
 * 공개 예정작 영화 Neo4j 저장 서비스
 * 영화 정보를 Neo4j 그래프 데이터베이스에 저장하고 관리
 * 
 * 주요 기능:
 * - 영화 노드 저장 및 업데이트
 * - 영화-배우, 영화-감독, 영화-작가, 영화-장르 관계 관리
 * - 기존 노드와 새로운 데이터 병합
 * 
 * 트랜잭션 관리:
 * - neo4jTransactionManager를 사용하여 트랜잭션 관리
 * - 노드와 관계의 저장/수정은 트랜잭션 내에서 실행
 * - 저장 실패 시 트랜잭션 롤백
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "neo4jTransactionManager")
public class UpcomingMovieSaveNeo4jService {

  private final UpcomingMovieNeo4jRepository movieRepository;
  private final UpcomingMovieGenreMergeHelper genreMergeHelper;
  private final UpcomingMovieMapper upcomingMovieMapper;

  // 영화 정보를 Neo4j에 저장
  // 기존 노드가 있으면 관계를 병합하고, 없으면 새로 생성
  public void saveToNeo4j(UpcomingMovieDto dto) {
    Optional<MovieNode> optional = movieRepository.findById(dto.getId());

    MovieNode toSave;
    if (optional.isPresent()) {
      // 기존 노드 + 관계 읽어오기
      MovieNode existing = optional.get();
      log.info("🔄 [saveToNeo4j] 기존 영화 업데이트: {} (ID={})", dto.getTitle(), dto.getId());

      // merge logic
      existing.setActors(mergeNames(existing.getActors(), dto.getActorNames(), ActorNode::new));
      existing.setDirectors(mergeNames(existing.getDirectors(), dto.getDirectorNames(), DirectorNode::new));
      existing.setWriters(mergeNames(existing.getWriters(), dto.getWriterNames(), WriterNode::new));
      existing.setGenres(genreMergeHelper.mergeGenres(existing.getGenres(), dto.getGenreIds()));

      toSave = existing;

    } else {
      // 새 노드 + 관계 생성
      log.info("🆕 [saveToNeo4j] 신규 영화 저장: {} (ID={})", dto.getTitle(), dto.getId());
      List<ActorNode>   actors   = dto.getActorNames().stream().map(ActorNode::new).toList();
      List<DirectorNode> directors = dto.getDirectorNames().stream().map(DirectorNode::new).toList();
      List<WriterNode>   writers  = dto.getWriterNames().stream().map(WriterNode::new).toList();
      List<GenreNode>    genres   = genreMergeHelper.mergeGenres(Collections.emptyList(), dto.getGenreIds());

      toSave = upcomingMovieMapper.toNeo4jNode(dto, actors, directors, writers, genres);
    }

    // MovieNode 하나만 save — 관계까지 전부 반영
    movieRepository.save(toSave);
    log.info("✅ [saveToNeo4j] 저장 완료: {} (ID={})", toSave.getTitle(), toSave.getId());
  }

  // 제네릭 병합 헬퍼 메서드
  // 기존 노드 목록과 새로운 이름 목록을 비교하여 중복 없이 병합
  // 리플렉션을 사용하여 getName() 메서드 호출
  private <N> List<N> mergeNames(
      List<N> existing, List<String> incoming, java.util.function.Function<String, N> ctor
  ) {
    Set<String> names = existing.stream()
        .map(n -> {
          try {
            // ActorNode#getName(), DirectorNode#getName() 등
            return (String) n.getClass().getMethod("getName").invoke(n);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toSet());

    for (String name : incoming) {
      if (!names.contains(name)) {
        existing.add(ctor.apply(name));
      }
    }
    return existing;
  }
}
