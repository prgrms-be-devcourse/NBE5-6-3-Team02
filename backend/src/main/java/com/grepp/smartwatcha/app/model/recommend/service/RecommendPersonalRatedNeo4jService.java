package com.grepp.smartwatcha.app.model.recommend.service;

import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieGenreNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendPersonalRatedNeo4jService {

    private final MovieGenreNeo4jRepository genreRepo;

    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public Map<Long, List<String>> getGenresByMovieIds(List<Long> movieIds) {
        return genreRepo.findAllById(movieIds).stream()
                .collect(Collectors.toMap(
                        MovieNode::getId,
                        m -> m.getGenres().stream().map(GenreNode::getName).toList()
                ));
    }

    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public Map<Long, List<String>> getTagsByMovieIds(List<Long> movieIds) {
        return genreRepo.findAllById(movieIds).stream()
                .collect(Collectors.toMap(
                        MovieNode::getId,
                        m -> m.getTags().stream().map(rel -> rel.getTagNode().getName()).toList()
                ));
    }
}