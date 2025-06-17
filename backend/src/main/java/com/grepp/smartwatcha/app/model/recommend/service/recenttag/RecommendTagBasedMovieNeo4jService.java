package com.grepp.smartwatcha.app.model.recommend.service.recenttag;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagResponse;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryNeo4jRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieTagCustomNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(value = "neo4jTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class RecommendTagBasedMovieNeo4jService {

    private final MovieQueryNeo4jRepository movieQueryNeo4jRepository;
    private final MovieTagCustomNeo4jRepository movieTagCustomNeo4jRepository;

    public List<String> findTagsByMovieId(Long movieId) {
        return movieQueryNeo4jRepository.findTagsByMovieId(movieId);
    }

    public List<MovieTagResponse> findTagsByMovieIdList(List<Long> movieIdList) {
        return movieTagCustomNeo4jRepository.findTagsByMovieIdList(movieIdList);
    }
}