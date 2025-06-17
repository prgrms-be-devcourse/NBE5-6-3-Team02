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
@Transactional(value = "neo4jTransactionManager", readOnly = true)
public class RecommendLatestRatedNeo4jService {

    private final MovieQueryNeo4jRepository genreRepo;

    // id로 장르노드 찾고 장르 목록 반환
    public List<String> getGenresByMovieId(Long movieId) {
        return genreRepo.findById(movieId)
                .map(MovieNode::getGenres)
                .orElse(List.of())
                .stream()
                .map(GenreNode::getName)
                .toList();
    }
}