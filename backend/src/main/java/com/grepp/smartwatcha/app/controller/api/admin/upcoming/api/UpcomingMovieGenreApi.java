package com.grepp.smartwatcha.app.controller.api.admin.upcoming.api;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieGenreApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "tmdb-genre-api",
    url = "https://api.themoviedb.org/3")
public interface UpcomingMovieGenreApi { // 장르

  @GetMapping("/genre/movie/list")
  UpcomingMovieGenreApiResponse getGenres(
      @RequestParam("api_key") String apiKey,
      @RequestParam("language") String language);

}
