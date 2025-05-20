package com.grepp.smartwatcha.app.model.recommend.service;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieGenreCustomNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
public class RecommendUserBasedRatedNeo4jService {

    private final MovieGenreCustomNeo4jRepository movieGenreCustomRepository;

    public List<MovieGenreTagResponse> getGenreTagInfoByMovieIdList(List<Long> movieIdList) {
        return movieGenreCustomRepository.findGenresAndTagsByMovieIdList(movieIdList);
    }
}
