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

/*
 * 공개 예정작 영화 장르 정보 조회 서비스
 * TMDB API 를 통해 장르 정보를 조회하고 메모리에 캐싱
 * 
 * 주요 기능:
 * - 애플리케이션 시작 시 장르 정보 초기화
 * - 장르 ID와 이름 매핑 제공
 * - 장르 정보 메모리 캐싱
 * 
 * 초기화 전략:
 * - @PostConstruct 로 애플리케이션 시작 시 자동 초기화
 * - TMDB API 호출 실패 시 로그만 기록하고 빈 맵 유지
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieGenreFetchNeo4jService {

  private final UpcomingMovieGenreApi movieGenreApi;

  @Value("${tmdb.api.key}")
  private String apiKey;

  // 장르 ID와 이름의 매핑을 저장하는 캐시
  private final Map<Long, String> genreMap = new HashMap<>();

  // 애플리케이션 시작 시 TMDB API를 호출하여 장르 정보 초기화
  // 초기화 실패 시 로그만 기록하고 빈 맵 유지
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

  // 캐시된 장르 ID-이름 매핑 반환
  public Map<Long, String> getGenreMap() {
    return genreMap;
  }
}
