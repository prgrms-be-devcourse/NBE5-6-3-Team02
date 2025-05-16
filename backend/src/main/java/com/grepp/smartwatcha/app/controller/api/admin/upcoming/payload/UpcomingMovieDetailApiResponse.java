package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class UpcomingMovieDetailApiResponse {
  @JsonProperty("origin_language")
  private String originLanguage;

  @JsonProperty("production_countries")
  private List<ProductionCountry> productionCountries;

  @Data
  public static class ProductionCountry {
    @JsonProperty("iso_3166_1")
    private String iso31661;
    private String name;
  }
}
