package com.grepp.smartwatcha.app.model.admin.movie.list.mapper;

import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieListResponseDto;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;

public class AdminMovieMapper {
  public static AdminMovieListResponseDto toDto(MovieEntity movie) {
   AdminMovieListResponseDto dto = AdminMovieListResponseDto.builder()
       .id(movie.getId())
       .title(movie.getTitle())
       .releaseDate(movie.getReleaseDate())
       .country(movie.getCountry())
       .poster(movie.getPoster())
       .isReleased(movie.getIsReleased())
       .certification(movie.getCertification())
       .overview(movie.getOverview())
       .build();

   boolean isRecentlyModified = movie.getModifiedAt() != null && movie.getModifiedAt().isAfter(LocalDateTime.now().minusDays(3));
   dto.setUpdatedRecently(isRecentlyModified);

   return dto;
  }
}
