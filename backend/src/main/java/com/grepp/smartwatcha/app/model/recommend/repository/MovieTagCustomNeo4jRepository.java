package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagResponse;

import java.util.List;

public interface MovieTagCustomNeo4jRepository {
    List<MovieTagResponse> findTagsByMovieIdList(List<Long> movieIdList);
}
