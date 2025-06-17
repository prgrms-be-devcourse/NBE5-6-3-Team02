package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class UpcomingMovieDto { // 공개 예정작 영화 정보 DTO

  private Long id;
  private String title;

  // 개봉일 문자열
  // 서비스 레이어에서 LocalDateTime으로 변환됨
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

  private String country;
  private String certification;

  private List<String> actorNames;
  private List<String> directorNames;
  private List<String> writerNames;
}
