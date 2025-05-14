package com.grepp.smartwatcha.infra.neo4j.repository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MovieNeo4jRepository extends Neo4jRepository<MovieNode, Long> {

    Optional<MovieNode> findByTitle(String title);
}