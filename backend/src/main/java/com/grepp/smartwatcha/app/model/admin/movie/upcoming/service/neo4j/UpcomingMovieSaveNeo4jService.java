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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "neo4jTransactionManager")
public class UpcomingMovieSaveNeo4jService {

  private final UpcomingMovieNeo4jRepository movieRepository;
  private final UpcomingMovieGenreMergeHelper genreMergeHelper;
  private final UpcomingMovieMapper upcomingMovieMapper;

  public void saveToNeo4j(UpcomingMovieDto dto) {
    Optional<MovieNode> optional = movieRepository.findById(dto.getId());

    MovieNode toSave;
    if (optional.isPresent()) {
      // 기존 노드 + 관계 읽어오기
      MovieNode existing = optional.get();
      log.info("🔁[saveToNeo4j] 병합 저장 시도: {}", dto.getTitle());

      // merge logic
      existing.setActors(mergeNames(existing.getActors(), dto.getActorNames(), ActorNode::new));
      existing.setDirectors(mergeNames(existing.getDirectors(), dto.getDirectorNames(), DirectorNode::new));
      existing.setWriters(mergeNames(existing.getWriters(), dto.getWriterNames(), WriterNode::new));
      existing.setGenres(genreMergeHelper.mergeGenres(existing.getGenres(), dto.getGenreIds()));

      toSave = existing;

    } else {
      // 새 노드 + 관계 생성
      log.info("🆕 [saveToNeo4j] 신규 저장 시도: {}", dto.getTitle());
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

  // Generic merge helper
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
