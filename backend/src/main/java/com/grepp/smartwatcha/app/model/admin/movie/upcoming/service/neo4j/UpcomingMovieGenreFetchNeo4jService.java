package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieGenreApi;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieGenreDto;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieGenreApiResponse;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieGenreFetchNeo4jService {

  private final UpcomingMovieGenreApi movieGenreApi;

  @Value("${tmdb.api.key}")
  private String apiKey;

  private final Map<Long, String> genreMap = new HashMap<>();

  @PostConstruct
  public void init() {
    try {
      UpcomingMovieGenreApiResponse response = movieGenreApi.getGenres(apiKey, "en");

      if (response != null && response.getGenres() != null) {
        for (UpcomingMovieGenreDto genre : response.getGenres()) {
          genreMap.put(genre.getId(), genre.getName());
        }
        log.info("✅ [장르 초기화] {}개 장르 로드 완료", genreMap.size());
      } else {
        log.warn("⚠️ [장르 초기화] 응답이 비어 있거나 장르가 없습니다.");
      }
    } catch (Exception e) {
      log.error("❌ [장르 초기화] TMDB 장르 API 호출 중 예외 발생", e);
    }
  }

  public Map<Long, String> getGenreMap() {
    return genreMap;
  }
}
