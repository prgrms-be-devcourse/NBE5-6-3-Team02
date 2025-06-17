package com.grepp.smartwatcha.app.controller.api.upcomingmovie.api

import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieCreditApiResponse
import com.grepp.smartwatcha.infra.config.FeignCommonConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/*
* TMDB 영화 출연진 정보 API를 위한 Feign Client
* TMDB API 에서 특정 영화의 출연진 정보를 가져옴
* 배우, 감독, 작가 등의 정보를 포함
*/
@FeignClient(
    name = "tmdb-credit-api",
    url = "https://api.themoviedb.org/3",
    configuration = [FeignCommonConfig::class]
)
interface UpcomingMovieCreditApi {
    // 특정 영화의 출연진 정보를 조회
    @GetMapping("/movie/{movieId}/credits")
    fun getCredits(
        @PathVariable("movieId") movieId: Long,
        @RequestParam("api_key") apiKey: String
    ): UpcomingMovieCreditApiResponse
}
