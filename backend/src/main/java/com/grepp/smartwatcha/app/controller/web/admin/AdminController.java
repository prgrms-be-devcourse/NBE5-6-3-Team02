package com.grepp.smartwatcha.app.controller.web.admin;

import com.grepp.smartwatcha.app.model.admin.AdminDashboardJpaService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.jpa.UpcomingMovieSyncTimeJpaRepository;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSyncTimeJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

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

    // 최근 동기화 시간 (형식: yyyy-MM-dd HH:mm)
    LocalDateTime syncTime = upcomingMovieSyncTimeJpaService.getLastSyncTime("upcoming");
    String formattedSyncTime = (syncTime != null) ?
        syncTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
    model.addAttribute("lastSyncTime", formattedSyncTime);

    // Sync 정보
    SyncTimeEntity upcomingSyncTime = upcomingMovieSyncTimeJpaRepository.findById("upcoming").orElse(null);
    model.addAttribute("lastSyncTime", syncTime != null ? upcomingSyncTime.getSyncTime() : null);
    model.addAttribute("newlyAddedCount", syncTime != null ? upcomingSyncTime.getNewlyAddedCount() : 0);
    model.addAttribute("failedCount", syncTime != null ? upcomingSyncTime.getFailedCount() : 0);

    // 최근 등록된 공개 예정작 (5개)
    List<MovieEntity> recentUpcoming = adminDashboardJpaService
        .getRecentUpcomingMovies();
    model.addAttribute("recentUpcomingMovies", recentUpcoming);

    return "admin/dashboard";
  }
}
