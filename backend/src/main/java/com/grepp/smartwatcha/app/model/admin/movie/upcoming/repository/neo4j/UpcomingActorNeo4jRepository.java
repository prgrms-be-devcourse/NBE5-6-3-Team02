package com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.neo4j;

import com.grepp.smartwatcha.infra.neo4j.node.ActorNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UpcomingActorNeo4jRepository extends Neo4jRepository<ActorNode, String> {

}
