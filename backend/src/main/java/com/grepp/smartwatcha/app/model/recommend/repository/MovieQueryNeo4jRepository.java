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

    // 영화 id에 대해 movieNode 조회
    Optional<MovieNode> findById(Long id);

    // 영화에 연결된 장르 조회
    @Query("MATCH (m:MOVIE)-[:HAS_GENRE]->(g:GENRE) WHERE m.id = $movieId RETURN g.name")
    List<String> findGenresByMovieId(@Param("movieId") Long movieId);
}