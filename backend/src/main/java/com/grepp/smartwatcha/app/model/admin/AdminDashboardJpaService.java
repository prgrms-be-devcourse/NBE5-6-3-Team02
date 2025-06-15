package com.grepp.smartwatcha.app.model.admin;

import com.grepp.smartwatcha.app.model.admin.movie.list.repository.AdminMovieJpaRepository;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.UpcomingMovieSyncTimeJpaRepository;
import com.grepp.smartwatcha.app.model.admin.tag.repository.AdminTagJpaRepository;
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
  private final AdminTagJpaRepository adminTagJpaRepository;
  private final UpcomingMovieSyncTimeJpaRepository upcomingMovieSyncTimeJpaRepository;

  public long getTotalUsers() { // 전체 유저 수 반환
    return adminUserJpaRepository.count();
  }

  public long getActiveUsers() { // 활성 유저 수 반환
    return adminUserJpaRepository.countByActivatedTrue();
  }

  public long getInactiveUsers() { // 비활성 유저 수 반환
    return adminUserJpaRepository.countByActivatedFalse();
  }

  public long getTotalMovies() { // 전체 영화 수 반환
    return adminMovieJpaRepository.count();
  }

  public long getUpcomingMovies() { // 현재 시점 이후 공개 예정 영화 수 반환
    return adminMovieJpaRepository.countByReleaseDateAfter(LocalDateTime.now());
  }

  public List<MovieEntity> getRecentUpcomingMovies() { // 최근 등록된 공개 예정작 5개 반환
    return adminMovieJpaRepository
        .findTop5ByReleaseDateAfterOrderByReleaseDateAsc(LocalDateTime.now());
  }

  public long getTotalTags() { // 전체 태그 수 반환
    return adminTagJpaRepository.count();
  }

  public int getNewlyAddedMovieCount() { // 최근 동기화에서 새로 추가된 영화 수 반환
    return upcomingMovieSyncTimeJpaRepository.findTopByTypeOrderBySyncTimeDesc("upcoming")
        .map(SyncTimeEntity::getNewlyAddedCount)
        .orElse(0);
  }

  public int getFailedSyncCount() { // 최근 동기화에서 실패한 영화 수 반환
    return upcomingMovieSyncTimeJpaRepository.findTopByTypeOrderBySyncTimeDesc("upcoming")
        .map(SyncTimeEntity::getFailedCount)
        .orElse(0);
  }
}
