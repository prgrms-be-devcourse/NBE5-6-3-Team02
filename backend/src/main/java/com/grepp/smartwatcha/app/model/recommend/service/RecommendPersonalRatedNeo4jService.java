package com.grepp.smartwatcha.app.model.recommend.service;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieGenreCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommendPersonalRatedNeo4jService {


    private final MovieGenreCustomRepository movieGenreCustomRepository;

    @Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
    public List<MovieGenreTagResponse> getGenreTagInfoByMovieIdList(List<Long> movieIdList) {
        return movieGenreCustomRepository.findGenresAndTagsByMovieIdList(movieIdList);
    }
}
