package com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto

/*
* TMDB 공개 예정작 API 응답 클래스
* TMDB API 에서 반환하는 공개 예정작 목록을 담는 응답 객체
* 페이지네이션 정보와 날짜 범위를 포함
*/
data class UpcomingMovieApiResponse(
    val dates: Dates,
    val page: Int,

    @field:JsonProperty("results")
    val movies: List<UpcomingMovieDto>,

    @field:JsonProperty("total_results")
    val totalResults: Int,

    @field:JsonProperty("total_pages")
    val totalPages: Int
) {
    data class Dates(
        val maximum: String,
        val minimum: String
    )
}
