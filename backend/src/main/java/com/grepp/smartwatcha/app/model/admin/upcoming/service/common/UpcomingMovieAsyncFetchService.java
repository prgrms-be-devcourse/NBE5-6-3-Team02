package com.grepp.smartwatcha.app.model.admin.upcoming.service.common;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieCreditApi;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieDetailApi;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieReleaseDateApi;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieCreditApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieDetailApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieReleaseDateApiResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpcomingMovieAsyncFetchService {

  //비동기 병렬 호출 서비스(실제 병렬 호출을 수행하는 비동기 서비스)
  private final UpcomingMovieCreditApi creditApi;
  private final UpcomingMovieReleaseDateApi releaseDateApi;
  private final UpcomingMovieDetailApi detailApi;

  @Async
  public CompletableFuture<UpcomingMovieCreditApiResponse> fetchCredits(Long movieId, String apiKey) {
    return CompletableFuture.completedFuture(creditApi.getCredits(movieId, apiKey));
  }

  @Async
  public CompletableFuture<UpcomingMovieReleaseDateApiResponse> fetchReleaseDates(Long movieId,
      String apiKey) {
    return CompletableFuture.completedFuture(releaseDateApi.getReleaseDates(movieId, apiKey));
  }

  @Async
  public CompletableFuture<UpcomingMovieDetailApiResponse> fetchDetails(Long movieId, String apiKey) {
    return CompletableFuture.completedFuture(detailApi.getDetails(movieId, apiKey));
  }
}
