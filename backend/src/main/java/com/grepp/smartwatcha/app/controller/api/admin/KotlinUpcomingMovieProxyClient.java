package com.grepp.smartwatcha.app.controller.api.admin;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// Kotlin 서버에서 enrich된 공개 예정작 데이터를 가져오는 Proxy Client
// - Spring 서버(Admin) → Kotlin 서버로 HTTP 요청
// - 내부 인증 토큰(x-internal-token) 사용
@Slf4j
@Component
@RequiredArgsConstructor
public class KotlinUpcomingMovieProxyClient {

  private final RestTemplate restTemplate;

  // Kotlin 서버 URL (e.g., http://localhost:8081)
  @Value("${internal.user-service-url}")
  private String kotlinUrl;

  // 내부 시스템 간 인증용 토큰
  @Value("${internal.token}")
  private String token;

  // Kotlin 서버에서 enrich된 공개 예정작 리스트 조회
  public List<UpcomingMovieDto> fetchFromKotlin() {
    String url = kotlinUrl + "/admin/movies/upcoming/sync";

    // 인증 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.set("x-internal-token", token);
    HttpEntity<Void> entity = new HttpEntity<>(headers);

    log.info("📤 Kotlin enrich 호출: {}", url);

    // Kotlin 서버로 POST 요청 → List<UpcomingMovieDto> 응답 수신
    ResponseEntity<List<UpcomingMovieDto>> response = restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        new ParameterizedTypeReference<>() {}  // 제네릭 타입 응답 처리
    );

    return response.getBody();  // 결과 반환
  }
}
