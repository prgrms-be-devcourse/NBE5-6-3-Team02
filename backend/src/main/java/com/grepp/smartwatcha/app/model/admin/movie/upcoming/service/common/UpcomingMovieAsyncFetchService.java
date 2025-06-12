package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieCreditApi;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieDetailApi;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieReleaseDateApi;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieCreditApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieDetailApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieReleaseDateApiResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * 공개 예정작 영화 정보 비동기 조회 서비스
 * TMDB API 의 여러 엔드포인트를 비동기로 호출하여 영화 상세 정보를 병렬로 조회
 * 
 * 주요 기능:
 * - 영화 크레딧 정보 비동기 조회
 * - 영화 개봉일 정보 비동기 조회
 * - 영화 상세 정보 비동기 조회
 * 
 * @Async 어노테이션을 사용하여 각 API 호출을 비동기로 처리
 * CompletableFuture 를 반환하여 호출자에서 결과를 기다릴 수 있음
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieAsyncFetchService {

  //비동기 병렬 호출 서비스
  private final UpcomingMovieCreditApi creditApi;
  private final UpcomingMovieReleaseDateApi releaseDateApi;
  private final UpcomingMovieDetailApi detailApi;

  // 영화의 크레딧 정보(배우, 감독, 작가)를 비동기로 조회
  @Async
  public CompletableFuture<UpcomingMovieCreditApiResponse> fetchCredits(Long movieId, String apiKey) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return creditApi.getCredits(movieId, apiKey);
      } catch (Exception e) {
        log.error("❌ [fetchCredits] 실패 - movieId: {}", movieId, e);
        return null;
      }
    });
  }

  // 영화의 개봉일 정보를 비동기로 조회
  @Async
  public CompletableFuture<UpcomingMovieReleaseDateApiResponse> fetchReleaseDates(Long movieId,
      String apiKey) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return releaseDateApi.getReleaseDates(movieId, apiKey);
      } catch (Exception e) {
        log.error("❌ [fetchReleaseDates] 실패 - movieId: {}", movieId, e);
        return null;
      }
    });
  }

  // 영화의 상세 정보를 비동기로 조회
  @Async
  public CompletableFuture<UpcomingMovieDetailApiResponse> fetchDetails(Long movieId, String apiKey) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return detailApi.getDetails(movieId, apiKey);
      } catch (Exception e) {
        log.error("❌ [fetchDetails] 실패 - movieId: {}", movieId, e);
        return null;
      }
    });
  }
}
