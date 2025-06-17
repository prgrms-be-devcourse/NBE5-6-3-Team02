package com.grepp.smartwatcha.infra.config

import feign.Request
import feign.RequestInterceptor
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

// Feign 클라이언트 공통 설정 클래스
@Configuration
@EnableFeignClients("com.grepp.smartwatcha")
class FeignCommonConfig {

    // Feign 요청 타임아웃 설정
    private val log = LoggerFactory.getLogger(FeignCommonConfig::class.java)

    @Bean
    fun requestOptions(): Request.Options {
        return Request.Options(
            5, TimeUnit.SECONDS,  // connect timeout
            10, TimeUnit.SECONDS, // read timeout
            true)                // follow redirects
    }

    // Feign 요청 인터셉터 설정
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate ->
            log.info("=========================================================")
            log.info("requestTemplate : {}", requestTemplate)
        }
    }
}
