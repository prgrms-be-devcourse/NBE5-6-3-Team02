package com.grepp.smartwatcha.app.controller.api.upcomingmovie.api

import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieGenreApiResponse
import com.grepp.smartwatcha.infra.config.FeignCommonConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

/*
* TMDB 영화 장르 API 를 위한 Feign Client
* TMDB API 에서 영화 장르 목록을 가져옴
*/
@FeignClient(
    name = "tmdb-genre-api",
    url = "https://api.themoviedb.org/3",
    configuration = [FeignCommonConfig::class]
)
interface UpcomingMovieGenreApi {
    //영화 장르 목록을 조회
    @GetMapping("/genre/movie/list")
    fun getGenres(
        @RequestParam("api_key") apiKey: String,
        @RequestParam("language") language: String
    ): UpcomingMovieGenreApiResponse
}
