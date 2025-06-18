package com.grepp.smartwatcha.app.controller.api.admin;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.UpcomingMovieUnifiedSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Kotlin 서버에서 enrich된 공개 예정작 정보를 가져와
//비동기 방식으로 Spring 서버 DB에 저장하는 API 컨트롤러
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/movies/upcoming")
public class UpcomingMovieProxySyncController {

  private final KotlinUpcomingMovieProxyClient proxyClient;
  private final UpcomingMovieUnifiedSaveService unifiedSaveService;

  // GET 방식으로 enrich + 저장 요청 (프론트 or 수동 호출)
  // - Kotlin 서버에 enrich 요청
  // - enrich된 결과를 비동기로 저장
  // ❗실시간 저장 완료를 보장하지 않음 (백그라운드 실행)
  @GetMapping("/sync-and-send")
  public ResponseEntity<String> proxySyncToKotlin() {
    log.info("📥 Kotlin enrich 요청 전송 시작");

    // 비동기 처리 (HTTP 응답은 즉시 반환)
    CompletableFuture.runAsync(() -> {
      try {
        // 1. Kotlin 서버에서 enrich된 DTO 리스트 수신
        List<UpcomingMovieDto> result = proxyClient.fetchFromKotlin();
        log.info("✅ Kotlin enrich 완료 수신: {}건", result.size());

        // 2. 통합 저장 서비스로 JPA + Neo4j 저장
        unifiedSaveService.saveAll(result);
        log.info("✅ 비동기 저장 완료");
      } catch (Exception e) {
        log.error("❌ 비동기 저장 중 예외 발생", e);
      }
    });

    // 요청은 즉시 성공 응답
    return ResponseEntity.ok("✅ enrich + 저장 요청 백그라운드 실행됨");
  }

  // POST 방식으로 enrich + 저장 요청 (프론트에서 직접 호출 시 사용)
  // - 내부 로직은 GET 방식과 동일
  @PostMapping("/proxy-sync")
  public ResponseEntity<String> proxySyncToKotlinPost() {
    log.info("📥 프론트에서 수동 동기화 요청 수신 (POST)");

    CompletableFuture.runAsync(() -> {
      try {
        List<UpcomingMovieDto> result = proxyClient.fetchFromKotlin();
        log.info("✅ Kotlin enrich 완료 수신: {}건", result.size());

        unifiedSaveService.saveAll(result);
        log.info("✅ 비동기 저장 완료");
      } catch (Exception e) {
        log.error("❌ 비동기 저장 중 예외 발생", e);
      }
    });

    return ResponseEntity.ok("✅ enrich + 저장 요청 백그라운드 실행됨");
  }
}
