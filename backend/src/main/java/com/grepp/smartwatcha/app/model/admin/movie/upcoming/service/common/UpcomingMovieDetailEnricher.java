package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common;

import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieCreditApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieDetailApiResponse;
import com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload.UpcomingMovieReleaseDateApiResponse;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieCastDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieCrewDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
 * 공개 예정작 영화 DTO 데이터 보강 서비스
 * TMDB API 에서 조회한 상세 정보를 DTO 에 추가하는 역할
 *
 * 주요 기능:
 * - 영화 크레딧 정보(배우, 감독, 작가) 보강
 * - 영화 등급 및 개봉 유형 정보 보강
 * - 영화 제작 국가 정보 보강
 *
 * 각 보강 메서드는 독립적으로 동작하며, 일부 정보 조회 실패 시에도 다른 정보는 보강 가능
 */
@Slf4j
@Component
public class UpcomingMovieDetailEnricher {

  /*
   * 영화의 크레딧 정보를 DTO 에 보강
   * - 상위 5명의 배우 이름 추가
   * - 감독 이름 추가
   * - 작가 이름 추가
   */
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
      log.warn("⚠️ [enrichCredits] 실패 - 영화 ID: {}, 제목: {}, 사유: {}", dto.getId(), dto.getTitle(),
          e.getMessage());
    }
  }

  /*
   * 영화의 등급 및 개봉 유형 정보를 DTO 에 보강
   * - 미국 기준 등급 정보 추가
   * - 미국 기준 개봉 유형 추가
   */
  public void enrichCertification(UpcomingMovieDto dto,
      UpcomingMovieReleaseDateApiResponse releaseData) {
    try {
      if (releaseData == null || releaseData.getResults() == null) {
        log.warn("⚠️ [enrichCertification] releaseData 또는 results 없음 - 영화 ID: {}", dto.getId());
        return;
      }

      releaseData.getResults().stream()
          .filter(r -> "US".equals(r.getIso_3166_1()))
          .flatMap(r -> r.getRelease_dates().stream())
          .filter(rd -> rd.getType() != null)
          .sorted(Comparator.comparing(
              UpcomingMovieReleaseDateApiResponse.ReleaseDateDetail::getReleaseDate))
          .findFirst()
          .ifPresent(rd -> dto.setReleaseType(rd.getType()));

      releaseData.getResults().stream()
          .filter(r -> "US".equals(r.getIso_3166_1()))
          .flatMap(r -> r.getRelease_dates().stream())
          .filter(rd -> rd.getCertification() != null && !rd.getCertification().isBlank())
          .sorted(Comparator.comparing(
              UpcomingMovieReleaseDateApiResponse.ReleaseDateDetail::getReleaseDate))
          .findFirst()
          .ifPresent(rd -> dto.setCertification(rd.getCertification()));

    } catch (Exception e) {
      log.warn("⚠️ [enrichCertification] 실패 - 영화 ID: {}, 제목: {}, 사유: {}", dto.getId(),
          dto.getTitle(), e.getMessage());
    }
  }

  /*
   * 영화의 제작 국가 정보를 DTO 에 보강
   * - 첫 번째 제작 국가의 ISO 코드 추가
   */
  public void enrichCountry(UpcomingMovieDto dto, UpcomingMovieDetailApiResponse detail) {
    try {
      if (detail != null && detail.getProductionCountries() != null
          && !detail.getProductionCountries().isEmpty()) {
        String countryCode = detail.getProductionCountries().get(0).getIso31661();
        dto.setCountry(countryCode);
      } else {
        log.warn("⚠️ [enrichCountry] country 정보 없음 - 영화 ID: {}", dto.getId());
      }
    } catch (Exception e) {
      log.warn("⚠️ [enrichCountry] 실패 - 영화 ID: {}, 제목: {}, 사유: {}", dto.getId(), dto.getTitle(),
          e.getMessage());
    }
  }
}
