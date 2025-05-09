package com.grepp.smartwatcha.app.service.recommend;

import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import com.grepp.smartwatcha.infra.neo4j.recommend.MovieGenreNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendLatestRatedNeo4jService {

    private final MovieGenreNeo4jRepository genreRepo;

    @Transactional("neo4jTransactionManager")
    public List<String> getGenresByMovieId(Long movieId) {
        return genreRepo.findById(movieId)
                .map(MovieNode::getGenres)
                .orElse(List.of())
                .stream()
                .map(GenreNode::getName)
                .toList();
    }
}
