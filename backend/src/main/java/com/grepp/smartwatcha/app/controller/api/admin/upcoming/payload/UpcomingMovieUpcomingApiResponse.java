package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import java.util.List;
import lombok.Data;

@Data
public class UpcomingMovieUpcomingApiResponse {

  private Dates dates;
  private int page;

  @JsonProperty("results")
  private List<UpcomingMovieDto> movies;

  @JsonProperty("total_results")
  private int totalResults;

  @JsonProperty("total_pages")
  private int totalPages;

  @Data
  public static class Dates {
    private String maximum;
    private String minimum;
  }
}
