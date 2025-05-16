package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.api.UpcomingMovieApi;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieCreditApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieDetailApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieReleaseDateApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieUpcomingApiResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpcomingMovieFetchService { // "영화 1편"의 정보를 한 DTO 에 채워주는 흐름 관리용 서비스

  private final UpcomingMovieAsyncFetchService asyncService;
  private final UpcomingMovieDetailEnricher dtoEnricher;
  private final UpcomingMovieApi upcomingMovieApi;

  @Value("${tmdb.api.key}")
  private String apiKey;

  public UpcomingMovieDto buildEnrichedDto(UpcomingMovieDto baseDto, String apiKey) throws ExecutionException, InterruptedException {
    Long movieId = baseDto.getId();

    // 병렬 호출 시작
    CompletableFuture<UpcomingMovieCreditApiResponse> creditsFuture = asyncService.fetchCredits(movieId, apiKey);
    CompletableFuture<UpcomingMovieReleaseDateApiResponse> releaseFuture = asyncService.fetchReleaseDates(movieId, apiKey);
    CompletableFuture<UpcomingMovieDetailApiResponse> detailFuture = asyncService.fetchDetails(movieId, apiKey);

    // 병령 완료 대기
    CompletableFuture.allOf(creditsFuture,releaseFuture, detailFuture).join();

    // 결과 꺼내기
    UpcomingMovieCreditApiResponse credits = creditsFuture.get();
    UpcomingMovieReleaseDateApiResponse releaseDate = releaseFuture.get();
    UpcomingMovieDetailApiResponse details = detailFuture.get();

    // DTO 채우기
    dtoEnricher.enrichCredits(baseDto, credits);
    dtoEnricher.enrichCertification(baseDto, releaseDate);
    dtoEnricher.enrichCountry(baseDto, details);

    return baseDto;
  }

  public List<UpcomingMovieDto> fetchUpcomingMovies() {
    int page = 1;
    UpcomingMovieUpcomingApiResponse response = upcomingMovieApi.getUpcomingMovies(apiKey, "en-US", page, "US");
    int totalPages = response.getTotalPages();

    List<UpcomingMovieDto> allMovies = new java.util.ArrayList<>(response.getMovies());
    for (page = 2; page <= totalPages; page++) {
        UpcomingMovieUpcomingApiResponse nextPage = upcomingMovieApi.getUpcomingMovies(apiKey, "en-US", page, "US");
        allMovies.addAll(nextPage.getMovies());
    }
    return allMovies;
  }
}
