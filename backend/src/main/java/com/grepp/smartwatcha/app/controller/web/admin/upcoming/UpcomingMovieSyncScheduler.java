package com.grepp.smartwatcha.app.controller.web.admin.upcoming;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common.UpcomingMovieFetchService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.common.UpcomingMovieUnifiedSaveService;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSyncTimeJpaService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpcomingMovieSyncScheduler {

  @Value("${tmdb.api.key}")
  private String apiKey;

  private final UpcomingMovieFetchService fetchService;
  private final UpcomingMovieUnifiedSaveService saveService;
  private final UpcomingMovieSyncTimeJpaService syncTimeService;

  @Scheduled(cron = "0 0 0 ? * MON") // 매주 월요일 00:00 스케줄러 작동
  public void syncAllUpcomingMovies() {
    log.info("🕒 [공개 예정작 스케줄러] 동기화 시작");

    List<UpcomingMovieDto> allMovies = fetchService.fetchUpcomingMovies();
    int total = allMovies.size();
    int success = 0;
    int skipped = 0;
    int failed = 0;

    List<String> skippedTitles = new ArrayList<>();
    List<String> skippedReasons = new ArrayList<>();

    for (UpcomingMovieDto baseDto : allMovies) {
      try {
        UpcomingMovieDto enrichedDto = fetchService.buildEnrichedDto(baseDto, apiKey);
        // enrich 후 releaseType 체크
        Integer type = enrichedDto.getReleaseType();
        if (type == null || !(type == 1 || type == 3 || type == 4)) {
          skippedTitles.add(enrichedDto.getTitle());
          skippedReasons.add("releaseType 조건 불일치 (type: " + type + ")");
          skipped++;
          continue;
        }

        if (!enrichedDto.getReleaseDateTime().toLocalDate().isAfter(LocalDate.now())) {
          skippedTitles.add(enrichedDto.getTitle());
          skippedReasons.add("ReleaseDate 조건 불일치 (date: " + enrichedDto.getReleaseDateTime().toLocalDate() + ")");
          skipped++;
          continue;
        }
        saveService.saveAll(enrichedDto);
        success++;

      } catch (Exception e) {
        failed++;
      }
    }
    syncTimeService.update("upcoming");

    // 요약 로그
    log.info("📊 [공개 예정작 동기화 요약]");
    log.info("✅ 저장 성공: {}건", success);
    log.info("⏭️ 스킵된 항목: {}건", skipped);
    log.info("❌ 저장 실패: {}건", failed);
    log.info("🎬 총 시도된 영화 수: {}건", total);

    for (int i = 0; i < skippedTitles.size(); i++) {
      log.info("⏭️ [{}] 스킵 사유: {}", skippedTitles.get(i), skippedReasons.get(i));
    }

  }
}