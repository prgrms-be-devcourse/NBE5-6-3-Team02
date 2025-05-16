package com.grepp.smartwatcha.app.controller.api.admin.upcoming.api;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieDetailApiResponse;
import com.grepp.smartwatcha.infra.config.FeignCommonConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "tmdb-detail-api",
    url = "https://api.themoviedb.org/3",
    configuration = {FeignCommonConfig.class}
)
public interface UpcomingMovieDetailApi { //상세 국가,제작 정보

  @GetMapping("/movie/{movieId}")
  UpcomingMovieDetailApiResponse getDetails(
      @PathVariable("movieId") Long movieId,
      @RequestParam("api_key") String apiKey
  );
}
