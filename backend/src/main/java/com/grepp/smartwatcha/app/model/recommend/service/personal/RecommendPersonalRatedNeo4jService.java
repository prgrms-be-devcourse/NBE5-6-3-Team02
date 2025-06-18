package com.grepp.smartwatcha.app.model.recommend.service.personal;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreDto;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagDto;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieGenreCustomNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "neo4jTransactionManager", readOnly = true)
public class RecommendPersonalRatedNeo4jService {

    private final MovieGenreCustomNeo4jRepository movieGenreCustomRepository;

    // 주어진 영화 목록에 대해 장르정보 반환
    public List<MovieGenreDto> getGenresByMovieIdList(List<Long> movieIds) {
        return movieGenreCustomRepository.findOnlyGenresByMovieIdList(movieIds);
    }

    // 주어진 영화 목록에 대해 태그정보 반환
    public List<MovieTagDto> getTagsByMovieIdList(List<Long> movieIds) {
        return movieGenreCustomRepository.findTagsByMovieIdList(movieIds);
    }
}