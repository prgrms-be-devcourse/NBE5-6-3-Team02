package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieQueryNeo4jRepository extends Neo4jRepository<MovieNode, Long> {

    Optional<MovieNode> findById(Long id);

    @Query("MATCH (m:MOVIE)-[:HAS_GENRE]->(g:GENRE) WHERE m.id = $movieId RETURN g.name")
    List<String> findGenresByMovieId(@Param("movieId") Long movieId);

    @Query("MATCH (m:MOVIE)-[:HAS_TAG]->(t:TAG) WHERE m.id = $movieId RETURN t.name")
    List<String> findTagsByMovieId(@Param("movieId") Long movieId);
}