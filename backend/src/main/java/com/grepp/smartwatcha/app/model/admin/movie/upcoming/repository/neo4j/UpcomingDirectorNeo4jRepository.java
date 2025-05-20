package com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.neo4j;

import com.grepp.smartwatcha.infra.neo4j.node.DirectorNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UpcomingDirectorNeo4jRepository extends Neo4jRepository<DirectorNode, String> {

}
