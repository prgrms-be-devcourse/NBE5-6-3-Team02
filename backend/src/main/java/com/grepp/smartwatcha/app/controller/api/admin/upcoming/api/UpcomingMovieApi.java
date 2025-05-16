package com.grepp.smartwatcha.app.controller.api.admin.upcoming.api;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieUpcomingApiResponse;
import com.grepp.smartwatcha.infra.config.FeignCommonConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "Upcoming-movie-api",
    url = "https://api.themoviedb.org/3",
    configuration = {FeignCommonConfig.class})
public interface UpcomingMovieApi {

  @GetMapping("/movie/upcoming")
  UpcomingMovieUpcomingApiResponse getUpcomingMovies(
      @RequestParam("api_key") String apiKey,
      @RequestParam("language") String language,
      @RequestParam("page") int page,
      @RequestParam("region") String region
  );
}
