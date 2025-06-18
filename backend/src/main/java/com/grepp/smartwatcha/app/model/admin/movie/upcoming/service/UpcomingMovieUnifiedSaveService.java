package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieSyncDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSaveJpaService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSyncTimeJpaService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.neo4j.UpcomingMovieSaveNeo4jService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Kotlin 서버에서 enrich된 공개 예정작 DTO를 받아와
// JPA(MySQL)와 Neo4j에 병렬 저장하고,
// 저장 성공/실패 통계를 관리하는 통합 저장 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieUnifiedSaveService {

  private final UpcomingMovieSaveJpaService jpaService;           // RDB 저장 서비스
  private final UpcomingMovieSaveNeo4jService neo4jService;       // Neo4j 저장 서비스
  private final UpcomingMovieSyncTimeJpaService syncTimeJpaService; // 동기화 시간 및 통계 저장 서비스

  // 개별 영화 DTO를 JPA와 Neo4j에 저장 (비동기 실행)
  // - JPA 저장 후 Neo4j 저장
  // - Neo4j 저장 실패 시, JPA 수동 롤백 시도
  @Async("taskExecutor")
  public CompletableFuture<Boolean> saveAsync(UpcomingMovieDto dto) {
    try {
      jpaService.saveToJpa(dto);
    } catch (Exception e) {
      log.error("❌ [JPA 저장 실패] 영화 ID: {}, 제목: {}", dto.getId(), dto.getTitle(), e);
      return CompletableFuture.completedFuture(false);
    }

    try {
      neo4jService.saveToNeo4j(dto);
    } catch (Exception e) {
      log.error("❌ [Neo4j 저장 실패] 영화 ID: {}, 제목: {}", dto.getId(), dto.getTitle(), e);
      try {
        // JPA 저장 내용 수동 롤백 (Neo4j 저장 실패 시 정합성 맞춤)
        jpaService.deleteFromJpaById(dto.getId());
        log.warn("🧹 JPA 저장 내용 수동 롤백 완료: ID={}", dto.getId());
      } catch (Exception rollbackEx) {
        log.error("❗ JPA 수동 롤백 중 예외 발생", rollbackEx);
      }
      return CompletableFuture.completedFuture(false);
    }

    return CompletableFuture.completedFuture(true);
  }

  // enrich된 DTO 리스트를 병렬로 저장 처리
  // - @Async + CompletableFuture + allOf 병렬 실행
  // - 실패한 ID 목록 수집
  // - 저장 시간 측정 및 로그 출력
  // - 동기화 통계 저장까지 포함
  public UpcomingMovieSyncDto saveAll(List<UpcomingMovieDto> dtoList) {
    StopWatch stopWatch = new StopWatch("병렬 저장 측정");
    stopWatch.start();

    int total = dtoList.size();
    List<Long> failedIds = new ArrayList<>();

    // DTO 각각을 병렬로 저장 실행
    List<CompletableFuture<SaveResult>> futures = dtoList.stream()
        .map(dto -> saveAsync(dto).thenApply(success -> new SaveResult(dto.getId(), success)))
        .toList();

    // 모든 저장이 완료될 때까지 대기
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    // 저장 결과 통계 계산
    int success = 0;
    int failed = 0;

    for (CompletableFuture<SaveResult> future : futures) {
      try {
        SaveResult result = future.join();
        if (result.success()) {
          success++;
        } else {
          failed++;
          failedIds.add(result.id());
        }
      } catch (Exception e) {
        failed++;
        log.warn("❌ 저장 실패 예외", e);
      }
    }

    stopWatch.stop();
    log.info("⏱️ 병렬 저장 완료 (총 {}건): {}ms", total, stopWatch.getTotalTimeMillis());

    // enrich는 Kotlin에서 처리하므로 enrich 실패는 0
    syncTimeJpaService.update("upcoming", success, failed, 0);

    // 최종 저장 결과 리턴
    return UpcomingMovieSyncDto.builder()
        .total(total)
        .success(success)
        .failed(failed)
        .enrichFailed(0)
        .skipped(0)
        .skippedIds(List.of())
        .skippedReasons(List.of())
        .failedIds(failedIds)
        .build();
  }

  // 내부 결과용 레코드 객체 (Java 16+ 사용 가능)
  private record SaveResult(Long id, boolean success) {}
}
