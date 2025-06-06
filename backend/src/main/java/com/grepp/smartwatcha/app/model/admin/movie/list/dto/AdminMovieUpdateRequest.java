package com.grepp.smartwatcha.app.model.admin.movie.list.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMovieUpdateRequest {
  private Long id;
  private String title;
  private String releaseDate;
  private String country;
  private String certification;
  private String overview;
  private String poster;
}
