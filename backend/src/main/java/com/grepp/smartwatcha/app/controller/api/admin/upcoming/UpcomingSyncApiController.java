package com.grepp.smartwatcha.app.controller.api.admin.upcoming;

import com.grepp.smartwatcha.app.controller.web.admin.movie.upcoming.UpcomingMovieSync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 공개 예정작 동기화를 위한 API 컨트롤러
 * TMDB 에서 공개 예정작 정보를 가져와 DB에 저장하는 동기화 작업을 수행
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/movies/upcoming")
public class UpcomingSyncApiController {

  private final UpcomingMovieSync scheduler;

  @PostMapping("/sync")
  public ResponseEntity<String> syncUpcomingManually() {
    try {
      log.info("Starting manual upcoming movies sync...");
      scheduler.syncAllUpcomingMovies();
      log.info("Manual upcoming movies sync completed successfully");
      return ResponseEntity.ok("✅ Manual synchronization completed successfully");
    } catch (Exception e) {
      log.error("Failed to sync upcoming movies: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body("❌ Synchronization failed: " + e.getMessage());
    }
  }
}
