package com.grepp.smartwatcha.app.model.admin.movie.list.mapper;

import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieListResponse;
import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieUpdateRequest;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class AdminMovieMapper {

  // MovieEntity → AdminMovieListResponse 변환
  //  - 영화 제목, 국가, 포스터 등 null-safe 처리
  //  - 최근 수정 여부(updatedRecently) 계산
  public static AdminMovieListResponse toDto(MovieEntity movie) {
    LocalDateTime now = LocalDateTime.now();
    boolean isRecentlyModified = movie.getModifiedAt() != null &&
        movie.getModifiedAt().isAfter(now.minusDays(3));

    return AdminMovieListResponse.builder()
        .id(movie.getId())
        .title(cleanOrNullText(movie.getTitle()))
        .releaseDate(movie.getReleaseDate())
        .country(cleanOrNullText(movie.getCountry()))
        .poster(cleanOrNullText(movie.getPoster()))
        .isReleased(movie.getIsReleased())
        .certification(cleanOrNullText(movie.getCertification()))
        .overview(cleanOrNullText(movie.getOverview()))
        .updatedRecently(isRecentlyModified)
        .build();
  }

  //  AdminMovieUpdateRequest → MovieEntity 변환
  //  - releaseDate 파싱 및 개봉 여부 판단 포함
  public static MovieEntity toEntity(AdminMovieUpdateRequest request) {
    LocalDateTime parsedDate = parseDateWithDefaultTime(request.getReleaseDate());
    boolean isReleased = isReleasedMovie(parsedDate);

    return MovieEntity.builder()
        .id(request.getId())
        .title(cleanOrNull(request.getTitle()))
        .releaseDate(parsedDate)
        .isReleased(isReleased)
        .country(cleanOrNull(request.getCountry()))
        .certification(cleanOrNull(request.getCertification()))
        .overview(cleanOrNull(request.getOverview()))
        .poster(cleanOrNull(request.getPoster()))
        .build();
  }

  // ===== 내부 유틸 =====

  private static String cleanOrNull(String value) {
    return (value == null || value.isBlank()) ? null : value.trim();
  }

  private static String cleanOrNullText(String value) {
    return (value == null || value.trim().isEmpty()) ? "<null>" : value.trim();
  }

  private static LocalDateTime parseDateWithDefaultTime(String dateStr) {
    try {
      if (dateStr == null || dateStr.isBlank()) {
        return null;
      }
      return LocalDate.parse(dateStr.trim()).atStartOfDay();
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  private static boolean isReleasedMovie(LocalDateTime dateTime) {
    return dateTime != null && dateTime.toLocalDate().isBefore(LocalDate.now().plusDays(1));
  }
}
