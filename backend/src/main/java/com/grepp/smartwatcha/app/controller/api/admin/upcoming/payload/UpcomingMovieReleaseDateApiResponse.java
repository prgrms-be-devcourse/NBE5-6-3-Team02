package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class UpcomingMovieReleaseDateApiResponse {
  private List<CountryReleaseDates> results;

  @Data
  public static class CountryReleaseDates {
    private String iso_3166_1;
    private List<ReleaseDateDetail> release_dates;
  }

  @Data
  public static class ReleaseDateDetail {
    @JsonProperty("certification")
    private String certification;

    @JsonProperty("type")
    private Integer type;

    @JsonProperty("release_date")
    private String releaseDate;
  }
}
