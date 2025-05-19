package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
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
  private List<Integer> genreIds;

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
    return LocalDate.parse(releaseDate).atStartOfDay(); // 00:00
  }
}
