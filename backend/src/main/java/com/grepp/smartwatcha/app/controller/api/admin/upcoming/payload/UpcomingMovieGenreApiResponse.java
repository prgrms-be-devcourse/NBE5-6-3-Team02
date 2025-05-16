package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.grepp.smartwatcha.app.model.admin.upcoming.dto.UpcomingMovieGenreDto;
import java.util.List;
import lombok.Data;

@Data
public class UpcomingMovieGenreApiResponse {
  private List<UpcomingMovieGenreDto> genres;
}
