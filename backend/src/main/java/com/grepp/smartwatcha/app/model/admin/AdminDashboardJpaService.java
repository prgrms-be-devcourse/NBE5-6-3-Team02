package com.grepp.smartwatcha.app.model.admin;

import com.grepp.smartwatcha.app.model.admin.movie.list.AdminMovieJpaRepository;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.jpa.UpcomingMovieSyncTimeJpaRepository;
import com.grepp.smartwatcha.app.model.admin.tag.AdminTagJpaRespository;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardJpaService {

  private final AdminUserJpaRepository adminUserJpaRepository;
  private final AdminMovieJpaRepository adminMovieJpaRepository;
  private final AdminTagJpaRespository adminTagJpaRespository;
  private final UpcomingMovieSyncTimeJpaRepository upcomingMovieSyncTimeJpaRepository;

  public long getTotalUsers() {
    return adminUserJpaRepository.count();
  }

  public long getActiveUsers() {
    return adminUserJpaRepository.countByActivatedTrue();
  }

  public long getInactiveUsers() {
    return adminUserJpaRepository.countByActivatedFalse();
  }

  public long getTotalMovies() {
    return adminMovieJpaRepository.count();
  }

  public long getUpcomingMovies() {
    return adminMovieJpaRepository.countByReleaseDateAfter(LocalDateTime.now());
  }

  public List<MovieEntity> getRecentUpcomingMovies() {
    return adminMovieJpaRepository
        .findTop5ByReleaseDateAfterOrderByReleaseDateAsc(LocalDateTime.now());
  }

  public long getTotalTags() {
    return adminTagJpaRespository.count();
  }

  public int getNewlyAddedMovieCount() {
    return upcomingMovieSyncTimeJpaRepository.findTopByTypeOrderBySyncTimeDesc("upcoming")
        .map(SyncTimeEntity::getNewlyAddedCount)
        .orElse(0);
  }

  public int getFailedSyncCount() {
    return upcomingMovieSyncTimeJpaRepository.findTopByTypeOrderBySyncTimeDesc("upcoming")
        .map(SyncTimeEntity::getFailedCount)
        .orElse(0);
  }
}
