package com.grepp.smartwatcha.app.model.admin.movie.list.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AdminMovieListResponse {
  private Long id;
  private String title;
  private LocalDateTime releaseDate;
  private String country;
  private String poster;
  private Boolean isReleased;
  private String certification;
  private String overview;
  private boolean updatedRecently;
}
