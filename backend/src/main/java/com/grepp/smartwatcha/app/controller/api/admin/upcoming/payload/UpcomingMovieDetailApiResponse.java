package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/*
 * TMDB 영화 상세 정보 API 응답 클래스
 * TMDB API 에서 반환하는 영화의 상세 정보를 담는 응답 객체
 * 원어, 제작 국가 등의 정보를 포함
 */
@Data
public class UpcomingMovieDetailApiResponse {

  @JsonProperty("origin_language")
  private String originLanguage;

  @JsonProperty("production_countries")
  private List<ProductionCountry> productionCountries;

  // 영화 제작 국가 정보를 담는 내부 클래스
  @Data
  public static class ProductionCountry {

    @JsonProperty("iso_3166_1")
    private String iso31661;
    private String name;
  }
}
