package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieGenreNeo4jRepository extends Neo4jRepository<MovieNode, Long> {
    Optional<MovieNode> findById(Long id);
}
