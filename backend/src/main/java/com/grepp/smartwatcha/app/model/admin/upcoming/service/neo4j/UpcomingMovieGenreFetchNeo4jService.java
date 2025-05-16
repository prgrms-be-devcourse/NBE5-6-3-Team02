package com.grepp.smartwatcha.app.model.admin.upcoming.service.neo4j;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieGenreApi;
import com.grepp.smartwatcha.app.model.admin.upcoming.dto.UpcomingMovieGenreDto;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieGenreApiResponse;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpcomingMovieGenreFetchNeo4jService {

  private final UpcomingMovieGenreApi movieGenreApi;

  @Value("${tmdb.api.key}")
  private String apiKey;

  private final Map<Integer, String> genreMap = new HashMap<>();

  @PostConstruct
  public void init() {
    UpcomingMovieGenreApiResponse response = movieGenreApi.getGenres(apiKey, "en");

    if (response != null || response.getGenres() != null) {
      for(UpcomingMovieGenreDto genre : response.getGenres()) {
        genreMap.put(genre.getId(), genre.getName());
      }
    }
  }

  public Map<Integer, String> getGenreMap() {
    return genreMap;
  }
}
