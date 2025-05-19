package com.grepp.smartwatcha.app.model.admin.movie.list.dto;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
public class AdminMovieUpdateRequestDto {
  private Long id;
  private String title;
  private String releaseDate;
  private String country;
  private String certification;
  private String overview;
  private String poster;

  public String clean(String value){
    return (value == null || value.isBlank()) ? null : value.trim();
  }

  private LocalDateTime parseDateWithDefaultTime() {
    try {
      if (releaseDate == null || releaseDate.isBlank()) return null;
      return LocalDate.parse(releaseDate.trim()).atStartOfDay();
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  private boolean isReleasedMovie(LocalDateTime parsedDate) {
    return parsedDate != null && parsedDate.toLocalDate().isBefore(LocalDate.now().plusDays(1));
  }

  public MovieEntity toEntity() {
    LocalDateTime parsedDate = parseDateWithDefaultTime();

    return MovieEntity.builder()
        .id(id)
        .title(clean(title))
        .releaseDate(parsedDate)
        .isReleased(isReleasedMovie(parsedDate))
        .country(clean(country))
        .certification(clean(certification))
        .overview(clean(overview))
        .poster(clean(poster))
        .build();
  }
}
