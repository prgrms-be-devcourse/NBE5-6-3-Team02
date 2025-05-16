package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.mapper.UpcomingMovieMapper;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.neo4j.UpcomingMovieNeo4jRepository;
import com.grepp.smartwatcha.infra.neo4j.node.*;
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

    if (optional.isPresent()) {
      MovieNode existing = optional.get();

      existing.setActors(mergeActors(existing.getActors(), dto.getActorNames()));
      existing.setDirectors(mergeDirectors(existing.getDirectors(), dto.getDirectorNames()));
      existing.setWriters(mergeWriters(existing.getWriters(), dto.getWriterNames()));
      existing.setGenres(genreMergeHelper.mergeGenres(existing.getGenres(), dto.getGenreIds()));

      //log.info("🔁 [Neo4j 병합 저장 시도] 영화 ID: {}, 제목: {}, 인증등급: {}, 타입: {}, 국가: {}", dto.getId(), dto.getTitle(), dto.getCertification(), dto.getReleaseType(), dto.getCountry());
      movieRepository.save(existing);
      //log.info("✅ [Neo4j 병합 저장 완료] 영화 ID: {}", dto.getId());
    } else {
      List<GenreNode> genreNodes = genreMergeHelper.mergeGenres(new ArrayList<>(), dto.getGenreIds());

      MovieNode node = upcomingMovieMapper.toNeo4jNode(
          dto,
          dto.getActorNames().stream().map(ActorNode::new).toList(),
          dto.getDirectorNames().stream().map(DirectorNode::new).toList(),
          dto.getWriterNames().stream().map(WriterNode::new).toList(),
          genreNodes
      );

      //log.info("🔁 [Neo4j 저장 시도] 영화 ID: {}, 제목: {}, 인증등급: {}, 타입: {}, 국가: {}", dto.getId(), dto.getTitle(), dto.getCertification(), dto.getReleaseType(), dto.getCountry());
      movieRepository.save(node);
      //log.info("✅ [Neo4j 저장 완료] 영화 ID: {}", dto.getId());
    }
  }

  private List<ActorNode> mergeActors(List<ActorNode> existing, List<String> incoming) {
    Set<String> names = existing.stream().map(ActorNode::getName).collect(Collectors.toSet());
    for (String name : incoming) {
      if (!names.contains(name)) {
        existing.add(new ActorNode(name));
      }
    }
    return existing;
  }

  private List<DirectorNode> mergeDirectors(List<DirectorNode> existing, List<String> incoming) {
    Set<String> names = existing.stream().map(DirectorNode::getName).collect(Collectors.toSet());
    for (String name : incoming) {
      if (!names.contains(name)) {
        existing.add(new DirectorNode(name));
      }
    }
    return existing;
  }

  private List<WriterNode> mergeWriters(List<WriterNode> existing, List<String> incoming) {
    Set<String> names = existing.stream().map(WriterNode::getName).collect(Collectors.toSet());
    for (String name : incoming) {
      if (!names.contains(name)) {
        existing.add(new WriterNode(name));
      }
    }
    return existing;
  }
}
