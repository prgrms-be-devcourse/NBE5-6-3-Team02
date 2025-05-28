package com.grepp.smartwatcha.app.model.details.repository.neo4jrepository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieDetailsNeo4jRepository extends Neo4jRepository<MovieNode, Long> {
    Optional<MovieNode> findById(Long movieId);

    @Query("""
MATCH (m:MOVIE {id: $movieId})-[:HAS_GENRE]->(g:GENRE)<-[:HAS_GENRE]-(other:MOVIE)
WHERE other.id <> $movieId
RETURN DISTINCT other.id
LIMIT 10
""")
    List<Long> findSimilarMoviesByGenre(Long movieId);
}
