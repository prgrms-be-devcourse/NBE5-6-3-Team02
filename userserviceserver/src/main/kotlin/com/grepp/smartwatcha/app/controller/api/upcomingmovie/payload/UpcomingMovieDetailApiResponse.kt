package com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload

import com.fasterxml.jackson.annotation.JsonProperty

/*
 * TMDB 영화 상세 정보 API 응답 클래스
 * TMDB API 에서 반환하는 영화의 상세 정보를 담는 응답 객체
 * 원어, 제작 국가 등의 정보를 포함
 */
data class UpcomingMovieDetailApiResponse(
    @field:JsonProperty("origin_language")
    val originLanguage: String?,

    @field:JsonProperty("production_countries")
    val productionCountries: List<ProductionCountry>?
) {
    // 영화 제작 국가 정보를 담는 내부 클래스
    data class ProductionCountry(
        @field:JsonProperty("iso_3166_1")
        val iso31661: String,
        val name: String
    )
}
