package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieCastDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieCrewDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieCreditApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieDetailApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieReleaseDateApiResponse;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpcomingMovieDetailEnricher {

  public void enrichCredits(UpcomingMovieDto dto, UpcomingMovieCreditApiResponse credits) {
    List<String> actorNames = credits.getCast().stream()
        .sorted(Comparator.comparingInt(UpcomingMovieCastDto::getOrder))
        .limit(5)
        .map(UpcomingMovieCastDto::getName)
        .toList();
    dto.setActorNames(actorNames);

    List<String> directorNames = credits.getCrew().stream()
        .filter(crew -> "Director".equalsIgnoreCase(crew.getJob()))
        .map(UpcomingMovieCrewDto::getName)
        .distinct()
        .toList();
    dto.setDirectorNames(directorNames);

    List<String> writerNames = credits.getCrew().stream()
        .filter(crew -> "Writer".equalsIgnoreCase(crew.getJob()) || "Screenplay".equalsIgnoreCase(
            crew.getJob()))
        .map(UpcomingMovieCrewDto::getName)
        .distinct()
        .toList();
    dto.setWriterNames(writerNames);
  }

  public void enrichCertification(UpcomingMovieDto dto,
      UpcomingMovieReleaseDateApiResponse releaseData) {
    if (releaseData == null || releaseData.getResults() == null) {
      System.out.println("releaseData 또는 results 없음");
      return;
    }

    // releaseType 먼저 설정
    releaseData.getResults().stream()
        .filter(r -> "US".equals(r.getIso_3166_1()))
        .flatMap(r -> r.getRelease_dates().stream())
        .filter(rd -> rd.getType() != null)
        .sorted(Comparator.comparing(
            UpcomingMovieReleaseDateApiResponse.ReleaseDateDetail::getReleaseDate))
        .findFirst()
        .ifPresent(rd -> dto.setReleaseType(rd.getType()));

    // certification 설정
    releaseData.getResults().stream()
        .filter(r -> "US".equals(r.getIso_3166_1()))
        .flatMap(r -> r.getRelease_dates().stream())
        .filter(rd -> rd.getCertification() != null && !rd.getCertification().isBlank())
        .sorted(Comparator.comparing(
            UpcomingMovieReleaseDateApiResponse.ReleaseDateDetail::getReleaseDate))
        .findFirst()
        .ifPresent(rd -> {
          dto.setCertification(rd.getCertification());
        });
  }

    public void enrichCountry (UpcomingMovieDto dto, UpcomingMovieDetailApiResponse detail){
      if (detail.getProductionCountries() != null && !detail.getProductionCountries().isEmpty()) {
        String countryCode = detail.getProductionCountries().get(0).getIso31661();
        dto.setCountry(countryCode);

      }
  }
}
