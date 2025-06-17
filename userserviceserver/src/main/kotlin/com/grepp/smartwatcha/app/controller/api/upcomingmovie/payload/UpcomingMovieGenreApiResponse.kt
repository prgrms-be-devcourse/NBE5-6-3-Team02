package com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload

import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieGenreDto

/*
 * TMDB 영화 장르 API 응답 클래스
 * TMDB API 에서 반환하는 영화 장르 목록을 담는 응답 객체
 */
data class UpcomingMovieGenreApiResponse(
    // 영화 장르 목록
    val genres: List<UpcomingMovieGenreDto>?
)
