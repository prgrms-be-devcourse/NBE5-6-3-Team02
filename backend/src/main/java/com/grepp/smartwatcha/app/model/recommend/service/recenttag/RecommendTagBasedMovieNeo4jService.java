package com.grepp.smartwatcha.app.model.recommend.service.recenttag;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(transactionManager = "neo4jTransactionManager")
@Service
@RequiredArgsConstructor
public class RecommendTagBasedMovieNeo4jService {

    private final MovieQueryNeo4jRepository movieQueryNeo4jRepository;

    public List<String> findTagsByMovieId(Long movieId) {
        return movieQueryNeo4jRepository.findTagsByMovieId(movieId);
    }

    public List<String> findGenresByMovieId(Long movieId) {
        return movieQueryNeo4jRepository.findGenresByMovieId(movieId);
    }
}