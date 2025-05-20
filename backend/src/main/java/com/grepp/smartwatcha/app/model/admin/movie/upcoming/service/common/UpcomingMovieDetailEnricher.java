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
    try {
      if (credits == null) {
        log.warn("⚠️ [enrichCredits] 응답이 null입니다. 영화 ID: {}", dto.getId());
        return;
      }

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
          .filter(crew -> "Writer".equalsIgnoreCase(crew.getJob()) ||
              "Screenplay".equalsIgnoreCase(crew.getJob()))
          .map(UpcomingMovieCrewDto::getName)
          .distinct()
          .toList();
      dto.setWriterNames(writerNames);

    } catch (Exception e) {
      log.warn("⚠️ [enrichCredits] 실패 - 영화 ID: {}, 제목: {}, 사유: {}", dto.getId(), dto.getTitle(), e.getMessage());
    }
  }

  public void enrichCertification(UpcomingMovieDto dto, UpcomingMovieReleaseDateApiResponse releaseData) {
    try {
      if (releaseData == null || releaseData.getResults() == null) {
        log.warn("⚠️ [enrichCertification] releaseData 또는 results 없음 - 영화 ID: {}", dto.getId());
        return;
      }

      releaseData.getResults().stream()
          .filter(r -> "US".equals(r.getIso_3166_1()))
          .flatMap(r -> r.getRelease_dates().stream())
          .filter(rd -> rd.getType() != null)
          .sorted(Comparator.comparing(UpcomingMovieReleaseDateApiResponse.ReleaseDateDetail::getReleaseDate))
          .findFirst()
          .ifPresent(rd -> dto.setReleaseType(rd.getType()));

      releaseData.getResults().stream()
          .filter(r -> "US".equals(r.getIso_3166_1()))
          .flatMap(r -> r.getRelease_dates().stream())
          .filter(rd -> rd.getCertification() != null && !rd.getCertification().isBlank())
          .sorted(Comparator.comparing(UpcomingMovieReleaseDateApiResponse.ReleaseDateDetail::getReleaseDate))
          .findFirst()
          .ifPresent(rd -> dto.setCertification(rd.getCertification()));

    } catch (Exception e) {
      log.warn("⚠️ [enrichCertification] 실패 - 영화 ID: {}, 제목: {}, 사유: {}", dto.getId(), dto.getTitle(), e.getMessage());
    }
  }

  public void enrichCountry(UpcomingMovieDto dto, UpcomingMovieDetailApiResponse detail) {
    try {
      if (detail != null && detail.getProductionCountries() != null && !detail.getProductionCountries().isEmpty()) {
        String countryCode = detail.getProductionCountries().get(0).getIso31661();
        dto.setCountry(countryCode);
      } else {
        log.warn("⚠️ [enrichCountry] country 정보 없음 - 영화 ID: {}", dto.getId());
      }
    } catch (Exception e) {
      log.warn("⚠️ [enrichCountry] 실패 - 영화 ID: {}, 제목: {}, 사유: {}", dto.getId(), dto.getTitle(), e.getMessage());
    }
  }
}
