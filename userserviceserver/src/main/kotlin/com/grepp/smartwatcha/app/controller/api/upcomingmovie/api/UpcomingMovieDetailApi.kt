package com.grepp.smartwatcha.app.controller.api.upcomingmovie.api

import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieDetailApiResponse
import com.grepp.smartwatcha.infra.config.FeignCommonConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/*
* TMDB 영화 상세 정보 API 를 위한 Feign Client
* TMDB API 에서 특정 영화의 상세 정보를 가져옴
* 제작 국가, 제작사 등 영화의 상세 정보를 포함
*/
@FeignClient(
    name = "tmdb-detail-api",
    url = "https://api.themoviedb.org/3",
    configuration = [FeignCommonConfig::class]
)
interface UpcomingMovieDetailApi {
    // 특정 영화의 상세 정보를 조회
    @GetMapping("/movie/{movieId}")
    fun getDetails(
        @PathVariable("movieId") movieId: Long,
        @RequestParam("api_key") apiKey: String
    ): UpcomingMovieDetailApiResponse
}
