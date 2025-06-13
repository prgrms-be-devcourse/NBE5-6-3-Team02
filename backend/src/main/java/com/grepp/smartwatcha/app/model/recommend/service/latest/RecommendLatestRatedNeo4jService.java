package com.grepp.smartwatcha.app.model.recommend.service.latest;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryNeo4jRepository;
import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendLatestRatedNeo4jService {

    private final MovieQueryNeo4jRepository genreRepo;

    // id로 장르노드 찾고 장르 목록 반환
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