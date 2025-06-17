package com.grepp.smartwatcha.infra.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.apache.hc.client5.http.config.RequestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

// RestTemplate 설정 클래스
// - Apache HttpClient 기반의 RestTemplate 설정
// - 커넥션 풀, 타임아웃 설정 등을 통해 안정적이고 확장 가능한 HTTP 클라이언트를 구성
@Configuration
public class RestTemplateConfig {

  // RestTemplate Bean 등록
  // - 커넥션 풀, 요청 타임아웃 등을 설정한 HttpClient 기반
  // - 다수의 외부 HTTP 호출 환경에서 성능/안정성 향상
  @Bean
  public RestTemplate restTemplate() {
    // 1. 커넥션 풀 설정
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(100);             // 전체 커넥션 수
    connectionManager.setDefaultMaxPerRoute(20);    // 라우트(호스트)당 최대 커넥션 수

    // 2. 요청 타임아웃 설정
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(Timeout.ofSeconds(5))        // 연결 시도 제한 시간
        .setResponseTimeout(Timeout.ofSeconds(120))     // ✅ 응답 수신 제한 시간 (기존보다 증가)
        .build();

    // 3. 커스텀 HttpClient 생성
    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setDefaultRequestConfig(requestConfig)
        .build();

    // 4. HttpClient 기반 RestTemplate 생성
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(factory);
  }
}
