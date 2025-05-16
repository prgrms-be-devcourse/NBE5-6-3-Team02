package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieCastDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieCrewDto;
import java.util.List;
import lombok.Data;

@Data
public class UpcomingMovieCreditApiResponse {
  private List<UpcomingMovieCastDto> cast;
  private List<UpcomingMovieCrewDto> crew;
}
