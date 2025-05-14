package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;

import java.util.List;

public interface MovieGenreCustomRepository {
    List<MovieGenreTagResponse> findGenresAndTagsByMovieIdList(List<Long> movieIdList);
}
