package com.grepp.smartwatcha.app.model.details.service;

import com.grepp.smartwatcha.app.model.details.repository.DetailsNeo4jRepository;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieNeo4jService {
    private final DetailsNeo4jRepository movieNeo4jRepository;

    public MovieNode getMovieWithAllRelations(Long movieId) {
        return movieNeo4jRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found in Neo4j"));
    }
}
