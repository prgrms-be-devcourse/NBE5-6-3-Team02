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
public class AdminMovieListResponseDto {
  private Long id;
  private String title;
  private LocalDateTime releaseDate;
  private String country;
  private String poster;
  private Boolean isReleased;
  private String certification;
  private String overview;
  private boolean updatedRecently;

  public String getTitle() {
    return (title == null || title.trim().isEmpty()) ? "<null>" : title.trim();
  }

  public String getPoster() {
    return (poster == null || poster.trim().isEmpty()) ? "<null>" : poster.trim();
  }

  public String getCountry() {
    return (country == null || country.trim().isEmpty()) ? "<null>" : country.trim();
  }
  public String getCertification() {
    return (certification == null || certification.trim().isEmpty()) ? "<null>" : certification.trim();
  }

  public String getOverview() {
    return (overview == null || overview.trim().isEmpty()) ? "<null>" : overview.trim();
  }
}
