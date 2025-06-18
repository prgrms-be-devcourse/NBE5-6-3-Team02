package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpcomingMovieSyncDto { // UpcomingMovieSync 작업 결과를 담는 DTO
  private int total;
  private int success;
  private int failed;
  private int skipped;
  private int enrichFailed;

  private List<Long> skippedIds;
  private List<String> skippedReasons;
  private List<Long> failedIds;
}
