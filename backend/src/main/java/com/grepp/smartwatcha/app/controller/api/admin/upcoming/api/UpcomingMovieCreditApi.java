package com.grepp.smartwatcha.app.controller.api.admin.upcoming.api;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieCreditApiResponse;
import com.grepp.smartwatcha.infra.config.FeignCommonConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "tmdb-credit-api",
    url = "https://api.themoviedb.org/3",
    configuration = {FeignCommonConfig.class}
)
public interface UpcomingMovieCreditApi { // 배우,감독,작가 정보

  @GetMapping("/movie/{movieId}/credits")
  UpcomingMovieCreditApiResponse getCredits(
      @PathVariable("movieId") Long movieId,
      @RequestParam("api_key") String apiKey
  );
}
