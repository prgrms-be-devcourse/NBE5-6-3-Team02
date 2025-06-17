package com.grepp.smartwatcha.app.model.upcomingmovie.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls

/**
 * 공개 예정작 영화 정보 DTO
 * TMDB API 에서 조회한 영화 정보를 담는 객체
 */
data class UpcomingMovieDto(
    val id:Long, // TMDB 영화 ID
    var title:String, //영화 제목

    // 개봉일 문자열 (실제 LocalDateTime 변환은 서비스 레이어에서 처리)
    @field:JsonProperty("release_date")
    var releaseDate:String? = null,

    @field:JsonProperty("poster_path")
    var posterPath:String? = null,

    var overview:String? = null,

    @field:JsonProperty("original_language")
    var originalLanguage: String? = null,

    @field:JsonProperty("genre_ids")
    var genreIds: List<Long>? = null,

    @field:JsonProperty("release_type")
    var releaseType: Int? = null,

    // from credits API
    var country: String? = null,
    var certification: String? = null,

    // neo4j 관련 (기본값: 빈 리스트)
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var actorNames: List<String> = emptyList(),
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var directorNames: List<String> = emptyList(),
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    var writerNames: List<String> = emptyList()
)
