package com.grepp.smartwatcha.app.model.recommend.service.highestrated;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryNeo4jRepository;
import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendHighestRatedNeo4jService {

    private final MovieQueryNeo4jRepository genreRepo;

    //movieId로 영화 노드 찾아 장르 노드들의 이름을 리스트로 반환
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