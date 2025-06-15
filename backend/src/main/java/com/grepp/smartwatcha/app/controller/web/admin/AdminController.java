package com.grepp.smartwatcha.app.controller.web.admin;

import com.grepp.smartwatcha.app.model.admin.AdminDashboardJpaService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.UpcomingMovieSyncTimeJpaRepository;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSyncTimeJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController { // 관리자 대시보드 페이지

  private final AdminDashboardJpaService adminDashboardJpaService;
  private final UpcomingMovieSyncTimeJpaService upcomingMovieSyncTimeJpaService;
  private final UpcomingMovieSyncTimeJpaRepository upcomingMovieSyncTimeJpaRepository;

  @GetMapping("/admin")
  public String showAdminDashboard(Model model) {

    // Summary 통계
    model.addAttribute("totalUsers", adminDashboardJpaService.getTotalUsers());
    model.addAttribute("activeUsers", adminDashboardJpaService.getActiveUsers());
    model.addAttribute("inactiveUsers", adminDashboardJpaService.getInactiveUsers());
    model.addAttribute("totalMovies", adminDashboardJpaService.getTotalMovies());
    model.addAttribute("upcomingMovies", adminDashboardJpaService.getUpcomingMovies());
    model.addAttribute("totalTags", adminDashboardJpaService.getTotalTags());

    // 동기화 정보 조회
    SyncTimeEntity sync = upcomingMovieSyncTimeJpaRepository.findById("upcoming").orElse(null);

    if (sync != null) {
      // 최근 동기화 시간: yyyy-MM-dd HH:mm 형식으로 포맷
      model.addAttribute("lastSyncTime", sync.getSyncTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

      // 동기화에서 새로 추가된 영화 수
      model.addAttribute("newlyAddedCount", sync.getNewlyAddedCount());

      // TMDB API 호출 실패 등으로 인해 저장 실패한 영화 수
      model.addAttribute("failedCount", sync.getFailedCount());

      // 상세 정보 보강(enrich) 중 예외 발생 → 저장 롤백된 영화 수
      model.addAttribute("enrichFailedCount", sync.getEnrichFailedCount());

    } else {
      // 동기화 정보가 없는 경우 기본값 설정
      model.addAttribute("lastSyncTime", "N/A");
      model.addAttribute("newlyAddedCount", 0);
      model.addAttribute("failedCount", 0);
      model.addAttribute("enrichFailedCount", 0);
    }

    // 최근 등록된 공개 예정작 (5개)
    List<MovieEntity> recentUpcoming = adminDashboardJpaService.getRecentUpcomingMovies();
    model.addAttribute("recentUpcomingMovies", recentUpcoming);

    return "admin/dashboard";
  }
}
