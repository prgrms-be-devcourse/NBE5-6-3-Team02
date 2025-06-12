package com.grepp.smartwatcha.app.model.details.service.neo4jservice;

import com.grepp.smartwatcha.app.model.details.repository.neo4jrepository.MovieDetailsNeo4jRepository;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieNeo4jService {
    private final MovieDetailsNeo4jRepository movieNeo4jRepository;


    public MovieNode getMovieWithAllRelations(Long movieId) {
        return movieNeo4jRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found in Neo4j"));
    }
    public List<Long> findSimilarMovieIds(Long movieId) {
        return movieNeo4jRepository.findSimilarMoviesByGenre(movieId);
    }
}
