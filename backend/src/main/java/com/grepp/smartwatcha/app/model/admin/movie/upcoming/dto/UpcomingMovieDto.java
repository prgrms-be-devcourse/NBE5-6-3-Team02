package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class UpcomingMovieDto {
  private Long id;
  private String title;

  @JsonProperty("release_date")
  private String releaseDate;

  @JsonProperty("poster_path")
  private String posterPath;

  private String overview;

  @JsonProperty("original_language")
  private String originalLanguage;

  @JsonProperty("genre_ids")
  private List<Long> genreIds;

  @JsonProperty("release_type")
  private Integer releaseType;

  //credits API
  private String country;
  private String certification;

  //neo4j
  private List<String> actorNames = new ArrayList<>();
  private List<String> directorNames = new ArrayList<>();
  private List<String> writerNames = new ArrayList<>();

  public LocalDateTime getReleaseDateTime() {
    if (releaseDate == null || releaseDate.isBlank()){
      log.warn("📅 releaseDate가 비어 있어 LocalDateTime으로 변환할 수 없습니다. [title: {}]", title);
      return null;
    }

    try {
      return LocalDate.parse(releaseDate).atStartOfDay(); // 00:00
    } catch (Exception e) {
      log.warn("📅 releaseDate 파싱 실패: '{}' [title: {}]", releaseDate, title);
      return null;
    }
  }
}
