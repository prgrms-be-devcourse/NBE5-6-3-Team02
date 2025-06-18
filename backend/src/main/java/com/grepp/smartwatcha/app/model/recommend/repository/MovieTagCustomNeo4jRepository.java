package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagDto;
import java.util.List;

public interface MovieTagCustomNeo4jRepository {

    // 영화에 연결된 태그 조회
    List<MovieTagDto> findTagsByMovieIdList(List<Long> movieIdList);
}