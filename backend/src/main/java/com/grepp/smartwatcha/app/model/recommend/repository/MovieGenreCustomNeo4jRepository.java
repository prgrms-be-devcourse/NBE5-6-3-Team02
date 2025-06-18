package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreDto;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagDto;

import java.util.List;

public interface MovieGenreCustomNeo4jRepository {

    // 영화 id로 장르 조회
    List<MovieGenreDto> findOnlyGenresByMovieIdList(List<Long> movieIdList);

    // 영화 id로 태그 조회
    List<MovieTagDto> findTagsByMovieIdList(List<Long> movieIdList);
}