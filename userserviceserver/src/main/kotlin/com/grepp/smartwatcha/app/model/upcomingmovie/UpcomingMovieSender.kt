package com.grepp.smartwatcha.app.model.upcomingmovie

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

// 공개 예정작 데이터를 Spring 서버로 전송하는 컴포넌트
// - 내부 인증 토큰(x-internal-token)을 포함하여 Spring 서버의 동기화 API로 데이터 전송
// - 요청 실패 시 로그 출력 및 실패 여부 반환
@Component
class UpcomingMovieSender(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {

    // 전송 대상 Spring 서버 URL (e.g., http://localhost:8080)
    @Value("\${spring.server.url}")
    private lateinit var springServerUrl: String

    // 내부 인증 토큰 (헤더에 포함)
    @Value("\${internal.token}")
    private lateinit var internalToken: String

    private val log = LoggerFactory.getLogger(UpcomingMovieSender::class.java)

    // 공개 예정작 리스트를 Spring 서버로 POST 요청 전송
    fun sendToSpring(dtoList: List<UpcomingMovieDto>): Boolean {
        val url = "$springServerUrl/admin/movies/upcoming/sync"

        // HTTP 헤더 구성: JSON 타입 + 내부 인증 토큰
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("x-internal-token", internalToken)
        }

        // 요청 본문과 헤더를 포함한 HttpEntity 생성
        val requestEntity = HttpEntity(dtoList, headers)

        return try {
            // POST 요청 전송 및 응답 수신
            val response = restTemplate.exchange<String>(
                url,
                HttpMethod.POST,
                requestEntity
            )

            log.info("✅ 스프링 서버에 동기화 결과 전송 성공. 응답 코드: {}", response.statusCode)
            true
        } catch (e: Exception) {
            log.error("❌ 스프링 서버 동기화 전송 실패: {}", e.message, e)
            false
        }
    }
}
