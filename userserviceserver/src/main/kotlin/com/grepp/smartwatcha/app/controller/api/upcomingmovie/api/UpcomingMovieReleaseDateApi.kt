package com.grepp.smartwatcha.app.controller.api.upcomingmovie.api

import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieReleaseDateApiResponse
import com.grepp.smartwatcha.infra.config.FeignCommonConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/*
* TMDB 영화 개봉일 및 관람등급 API 를 위한 Feign Client
* TMDB API 에서 특정 영화의 개봉일과 관람등급 정보를 가져옴
* 각 국가별 개봉일과 관람등급 정보를 포함
*/
@FeignClient(
    name = "tmdb-release-api",
    url = "https://api.themoviedb.org/3",
    configuration = [FeignCommonConfig::class]
)
interface UpcomingMovieReleaseDateApi {
    // 특정 영화의 개봉일과 관람등급 정보를 조회
    @GetMapping("/movie/{movieId}/release_dates")
    fun getReleaseDates(
        @PathVariable("movieId") movieId: Long,
        @RequestParam("api_key") apiKey: String
    ): UpcomingMovieReleaseDateApiResponse
}
