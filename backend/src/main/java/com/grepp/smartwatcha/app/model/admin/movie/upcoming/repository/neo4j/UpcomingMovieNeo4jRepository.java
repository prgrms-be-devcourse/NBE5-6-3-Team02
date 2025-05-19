package com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.neo4j;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UpcomingMovieNeo4jRepository extends Neo4jRepository<MovieNode, Long> {
  Optional<MovieNode> findById(Integer tmdbId);
}
