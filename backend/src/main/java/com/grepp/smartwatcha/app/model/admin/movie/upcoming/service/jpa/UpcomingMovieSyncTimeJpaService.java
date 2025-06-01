package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.jpa.UpcomingMovieSyncTimeJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * 공개 예정작 영화 동기화 시간 관리 서비스
 * 영화 정보 동기화 작업의 실행 시간과 결과를 기록하고 조회
 * 
 * 주요 기능:
 * - 동기화 작업 시간 기록
 * - 동기화 결과 통계 저장 (신규 추가, 실패, 보강 실패 건수)
 * - 마지막 동기화 시간 조회
 */
@Service
@RequiredArgsConstructor
public class UpcomingMovieSyncTimeJpaService {
  private final UpcomingMovieSyncTimeJpaRepository upcomingMovieSyncTimeJpaRepository;

  // 동기화 작업 결과를 저장하고 마지막 동기화 시간을 업데이트
  public void update(String type, int newlyAddedCount, int failedCount, int enrichFailed) {
    SyncTimeEntity entity = upcomingMovieSyncTimeJpaRepository.findById(type)
        .orElse(SyncTimeEntity.builder().type(type).build());

    entity.setSyncTime(LocalDateTime.now());
    entity.setNewlyAddedCount(newlyAddedCount);
    entity.setFailedCount(failedCount);
    entity.setEnrichFailedCount(enrichFailed);

    upcomingMovieSyncTimeJpaRepository.save(entity);
  }

  // 특정 동기화 유형의 마지막 실행 시간을 조회
  public LocalDateTime getLastSyncTime(String type) {
    return upcomingMovieSyncTimeJpaRepository.findById(type)
        .map(SyncTimeEntity::getSyncTime)
        .orElse(null);
  }
}
