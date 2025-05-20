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

@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieAsyncFetchService {

  //비동기 병렬 호출 서비스
  private final UpcomingMovieCreditApi creditApi;
  private final UpcomingMovieReleaseDateApi releaseDateApi;
  private final UpcomingMovieDetailApi detailApi;

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
