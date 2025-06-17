package com.grepp.smartwatcha.app.model.recommend.service.recentgenre;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(value = "neo4jTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class RecommendGenreBasedMovieNeo4jService {

    private final MovieQueryNeo4jRepository neo4jRepository;

    // 영화에 연관된 장르 조회
    public List<String> findGenresByMovieId(Long movieId) {
        return neo4jRepository.findGenresByMovieId(movieId);
    }
}