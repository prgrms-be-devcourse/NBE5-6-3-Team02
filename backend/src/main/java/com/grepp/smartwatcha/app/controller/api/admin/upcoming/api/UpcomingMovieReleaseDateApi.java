package com.grepp.smartwatcha.app.controller.api.admin.upcoming.api;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieReleaseDateApiResponse;
import com.grepp.smartwatcha.infra.config.FeignCommonConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "tmdb-release-api",
    url = "https://api.themoviedb.org/3",
    configuration = {FeignCommonConfig.class}
)
public interface UpcomingMovieReleaseDateApi { //관람등급 정보

  @GetMapping("/movie/{movieId}/release_dates")
  UpcomingMovieReleaseDateApiResponse getReleaseDates(
      @PathVariable("movieId") Long movieId,
      @RequestParam("api_key") String apiKey
  );
}
