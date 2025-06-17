package com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload

import com.fasterxml.jackson.annotation.JsonProperty

/*
 * TMDB 영화 개봉일 및 관람등급 API 응답 클래스
 * TMDB API 에서 반환하는 영화의 개봉일과 관람등급 정보를 담는 응답 객체
 * 국가별 개봉일과 관람등급 정보를 포함
 */
data class UpcomingMovieReleaseDateApiResponse(
    val results: List<CountryReleaseDates>? = null,
) {
    data class CountryReleaseDates( //국가별 개봉 정보를 담는 내부 클래스
        val iso_3166_1: String,
        val release_dates: List<ReleaseDateDetail>
    )

    data class ReleaseDateDetail( //  개봉 정보 상세를 담는 내부 클래스(관람등급, 개봉 유형, 개봉일)
        @field:JsonProperty("certification")
        val certification: String? = null,

        @field:JsonProperty("type")
        val type: Int? = null,

        @field:JsonProperty("release_date")
        val releaseDate: String? = null
    )
}
