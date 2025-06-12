package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import java.util.List;

public interface MovieGenreCustomNeo4jRepository {
    // 영화 id로 장르와 태그 조회
    List<MovieGenreTagResponse> findGenresAndTagsByMovieIdList(List<Long> movieIdList);
}