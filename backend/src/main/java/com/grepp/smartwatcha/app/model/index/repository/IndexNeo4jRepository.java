package com.grepp.smartwatcha.app.model.index.repository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexNeo4jRepository extends Neo4jRepository<MovieNode, Long> {

    @Query("MATCH (m:MOVIE)-[:HAS_GENRE]->(g:GENRE),\n"
            + "      (m)-[:HAS_TAG]->(t:TAG)\n"
            + "WHERE g.name IN [\"Comedy\", \"Action\"]\n"
            + "  AND t.name IN [\"funny\", \"family\"]\n"
            + "RETURN DISTINCT m.id ORDER BY rand() LIMIT 10")
    List<Long> findLightMovies();
}
