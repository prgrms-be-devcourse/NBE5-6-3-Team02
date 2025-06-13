package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpcomingMovieSyncDto { // UpcomingMovieSync 작업 결과를 담는 DTO
  private int total;
  private int success;
  private int failed;
  private int skipped;
  private int enrichFailed;

  private List<Long> failedIds;
  private List<Long> skippedIds;
  private List<String> skippedReasons;
}
