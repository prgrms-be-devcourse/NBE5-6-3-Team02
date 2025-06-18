package com.grepp.smartwatcha.app.controller.api.upcomingmovie.api

import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieApiResponse
import com.grepp.smartwatcha.infra.config.FeignCommonConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/*
* TMDB 공개 예정작 API 를 위한 Feign Client
* TMDB API 에서 공개 예정작 목록을 가져옴
*/
@FeignClient(
    name = "tmdb-upcoming-api",
    url = "https://api.themoviedb.org/3",
    configuration = [FeignCommonConfig::class]
)
interface UpcomingMovieApi {
    // 공개 예정작 목록을 조회
    @GetMapping("/movie/upcoming")
    fun getUpcomingMovies(
        @RequestParam("api_key") apiKey: String,
        @RequestParam("language") language: String,
        @RequestParam("page") page: Int,
        @RequestParam("region") region: String
    ): UpcomingMovieApiResponse
}
