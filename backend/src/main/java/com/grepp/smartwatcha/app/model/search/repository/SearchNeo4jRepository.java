package com.grepp.smartwatcha.app.model.search.repository;

import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchNeo4jRepository extends Neo4jRepository<MovieNode, Long> {

    @Query("""
            MATCH (m:MOVIE)-[:HAS_GENRE]->(g:GENRE)
            WHERE lower(g.name) = lower($genre)
            RETURN m
            """)
    List<MovieNode> findByGenre(@Param("genre") String genre);

    @Query("""
            MATCH (m:MOVIE)-[:ACTED_IN]->(a:ACTOR)
            WHERE lower(a.name) = lower($actor)
            RETURN m
            """)
    List<MovieNode> findByActor(@Param("actor") String actor);

    @Query("""
            MATCH (m:MOVIE)-[:DIRECTED_BY]->(d:DIRECTOR)
            WHERE lower(d.name) = lower($director)
            RETURN m
            """)
    List<MovieNode> findByDirector(@Param("director") String director);

    @Query("""
            MATCH (m:MOVIE)-[:WRITTEN_BY]->(w:WRITER)
            WHERE lower(w.name) = lower($writer)
            RETURN m
            """)
    List<MovieNode> findByWriter(String writer);
}
