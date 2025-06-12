package com.grepp.smartwatcha.app.model.recommend.service.personal;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieGenreCustomNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
public class RecommendPersonalRatedNeo4jService {
    
    private final MovieGenreCustomNeo4jRepository movieGenreCustomRepository;

    // 각 영화의 장르 와 태그 가져옴 
    public List<MovieGenreTagResponse> getGenreTagInfoByMovieIdList(List<Long> movieIdList) {
        return movieGenreCustomRepository.findGenresAndTagsByMovieIdList(movieIdList);
    }
}