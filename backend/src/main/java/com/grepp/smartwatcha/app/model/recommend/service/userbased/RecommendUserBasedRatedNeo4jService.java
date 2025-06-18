package com.grepp.smartwatcha.app.model.recommend.service.userbased;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreDto;
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

    // 각 영화의 장르 조회
    public List<MovieGenreDto> getGenresByMovieIdList(List<Long> movieIdList) {
        return movieGenreCustomRepository.findOnlyGenresByMovieIdList(movieIdList);
    }
}