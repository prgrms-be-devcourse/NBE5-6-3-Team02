package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.UpcomingMovieSyncTimeJpaRepository;
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

  // 동기화 작업 결과를 저장하고 마지막 동기화 시간을 업데이트
  private final UpcomingMovieSyncTimeJpaRepository syncTimeJpaRepository;

  // 새로운 방식 - 통계 기반
  public void update(String type, int successCount, int failedCount, int enrichFailedCount) {
    SyncTimeEntity entity = SyncTimeEntity.builder()
        .type(type)
        .syncTime(LocalDateTime.now())
        .newlyAddedCount(successCount)
        .failedCount(failedCount)
        .enrichFailedCount(enrichFailedCount)
        .build();
    syncTimeJpaRepository.save(entity);
  }
}
