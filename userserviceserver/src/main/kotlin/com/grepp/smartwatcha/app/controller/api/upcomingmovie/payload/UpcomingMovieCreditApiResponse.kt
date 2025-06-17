package com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload

import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieCastDto
import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieCrewDto

/*
 * TMDB 영화 출연진 API 응답 클래스
 * TMDB API 에서 반환하는 영화 출연진 정보를 담는 응답 객체
 * 배우(cast)와 제작진(crew) 정보를 포함
 */
data class UpcomingMovieCreditApiResponse(
    // 영화 출연 배우 목록
    val cast: List<UpcomingMovieCastDto>?,
    // 영화 제작진 목록 (감독, 작가 등)
    val crew: List<UpcomingMovieCrewDto>?
)
