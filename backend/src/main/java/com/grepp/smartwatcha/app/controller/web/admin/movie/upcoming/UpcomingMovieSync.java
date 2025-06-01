package com.grepp.smartwatcha.app.controller.web.admin.movie.upcoming;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.mapper.UpcomingMovieMapper;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common.UpcomingMovieFetchService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common.UpcomingMovieUnifiedSaveService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSyncTimeJpaService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/*
 * 공개 예정작 동기화 스케줄러
 * TMDB API 에서 공개 예정작 정보를 주기적으로 동기화
 *
 * 동기화 주기: 매주 월요일 00:00
 * 동기화 대상:
 * - releaseType 이 1, 3, 4인 영화
 * - 현재 날짜 이후 개봉 예정인 영화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieSync {

  @Value("${tmdb.api.key}")
  private String apiKey;

  private final UpcomingMovieFetchService fetchService;
  private final UpcomingMovieUnifiedSaveService saveService;
  private final UpcomingMovieSyncTimeJpaService syncTimeService;
  private final UpcomingMovieMapper mapper;

  /*
   * 공개 예정작 전체 동기화
   *
   * 동기화 과정:
   * 1. TMDB API 에서 공개 예정작 목록 조회
   * 2. 각 영화에 대해:
   *    - 상세 정보 조회 (enrich)
   *    - 검증 (개봉 타입, 개봉일)
   *    - DB 저장
   * 3. 동기화 결과 기록
   *    - 성공/실패/스킵 건수
   *    - 스킵된 영화 목록과 사유
   *
   * 검증 조건:
   * - 개봉 타입: 1(개봉), 3(제한적 개봉), 4(디지털 개봉)
   * - 개봉일: 현재 날짜 이후
   */
  @Scheduled(cron = "0 0 0 ? * MON") // 매주 월요일 00:00 스케줄러 작동
  public void syncAllUpcomingMovies() {
    log.info("🕒 [공개 예정작] 동기화 시작");

    List<UpcomingMovieDto> allMovies = fetchService.fetchUpcomingMovies();
    int total = allMovies.size();
    int success = 0;
    int skipped = 0;
    int failed = 0;
    int enrichFailed = 0;

    List<Long> skippedIds = new ArrayList<>();
    List<String> skippedTitles = new ArrayList<>();
    List<String> skippedReasons = new ArrayList<>();

    for (UpcomingMovieDto baseDto : allMovies) {
      try {
        UpcomingMovieDto enrichedDto = fetchService.buildEnrichedDto(baseDto, apiKey);

        if (enrichedDto == null) {
          enrichFailed++;
          log.warn("⚠️ enrich 결과가 null 이어서 해당 영화 건너뜀: {}", baseDto.getTitle(), baseDto.getId());
          continue;
        }

        Integer type = enrichedDto.getReleaseType();

        if (type == null || !(type == 1 || type == 3
            || type == 4)) { // 개봉 타입이 1(개봉), 3(제한적 개봉), 4(디지털 개봉)가 아닌경우
          skippedIds.add(enrichedDto.getId());
          skippedTitles.add(enrichedDto.getTitle());
          skippedReasons.add("releaseType 조건 불일치 (type: " + type + ")");
          skipped++;
          continue;
        }

        // 날짜 변환 로직을 mapper 를 통해 처리
        LocalDateTime releaseDateTime = mapper.parseDateWithDefaultTime(enrichedDto.getReleaseDate(), enrichedDto.getTitle());
        if (releaseDateTime == null || !releaseDateTime.toLocalDate().isAfter(LocalDate.now())) {
          skippedIds.add(enrichedDto.getId());
          skippedTitles.add(enrichedDto.getTitle());
          skippedReasons.add("ReleaseDate 조건 불일치 (date: " + enrichedDto.getReleaseDate() + ")");
          skipped++;
          continue;
        }

        saveService.saveAll(enrichedDto);
        success++;

      } catch (Exception e) {
        log.error("❌ [{}] 저장 실패: {}", baseDto.getTitle(), e.getMessage(), e);
        failed++;
      }
    }

    syncTimeService.update("upcoming", success, failed, enrichFailed);

    // 요약 로그
    log.info("=========================================================================");
    log.info("📊 [공개 예정작 동기화 요약]");
    log.info("✅ 저장 성공: {}건", success);
    log.info("⏭️ 스킵된 항목: {}건", skipped);
    log.info("⚠️ enrich 실패: {}건", enrichFailed);
    log.info("❌ 저장 실패: {}건", failed);
    log.info("🎬 총 시도된 영화 수: {}건", total);
    log.info("=========================================================================");

    for (int i = 0; i < skippedTitles.size(); i++) {
      log.info("⏭️ [{}] {} - 스킵 사유: {}",
          skippedIds.get(i),
          skippedTitles.get(i),
          skippedReasons.get(i));
    }
  }
}
