package com.grepp.smartwatcha.infra.config

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.core5.util.Timeout
import org.apache.hc.client5.http.config.RequestConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

// RestTemplate 설정 클래스
@Configuration
class RestTemplateConfig {

    // RestTemplate Bean 등록
    @Bean
    fun restTemplate(): RestTemplate {
        // 커넥션 풀 매니저 설정
        val connectionManager = PoolingHttpClientConnectionManager().apply {
            maxTotal = 100               // 전체 커넥션 최대 수
            defaultMaxPerRoute = 20     // 라우트(호스트)당 최대 커넥션 수
        }

        // 요청별 타임아웃 설정
        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(Timeout.ofSeconds(5))    // 연결 타임아웃 (서버에 접속까지 대기 시간)
            .setResponseTimeout(Timeout.ofSeconds(100)) // 응답 수신 타임아웃 (서버 응답 대기 시간)
            .build()

        // HttpClient 생성
        val httpClient: CloseableHttpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build()

        // HttpClient 기반 RequestFactory 생성
        val factory = HttpComponentsClientHttpRequestFactory(httpClient)

        // RestTemplate 생성 및 반환
        return RestTemplate(factory)
    }
}
