package com.grepp.smartwatcha.app.controller.api.admin.upcoming;

import com.grepp.smartwatcha.app.controller.web.admin.upcoming.UpcomingMovieSyncScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/movies/upcoming")
public class UpcomingSyncApiController {
  // 수동 전체 동기화 : 수동으로 TMDB 에서 공개 예정작을 가져와 DB에 저장

  private final UpcomingMovieSyncScheduler scheduler;

  @PostMapping("/sync-manual")
  public String syncUpcomingManually() {
    scheduler.syncAllUpcomingMovies();
    return "✅ 수동 동기화 완료 (GET)";
  }

  @GetMapping("/sync-manual-test")
  public String syncTest() {
    scheduler.syncAllUpcomingMovies();
    return "✅ 수동 동기화 (GET)";
  }
}
