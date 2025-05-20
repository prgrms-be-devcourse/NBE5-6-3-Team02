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

@Component
@RequiredArgsConstructor
public class UpcomingMovieGenreMergeHelper {

  private final UpcomingMovieGenreFetchNeo4jService genreFetchService;

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
