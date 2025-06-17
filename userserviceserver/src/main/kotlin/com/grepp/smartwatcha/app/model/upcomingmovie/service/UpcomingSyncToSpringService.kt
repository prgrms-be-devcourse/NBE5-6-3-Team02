package com.grepp.smartwatcha.app.model.upcomingmovie.service

import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.http.HttpMethod

@Service
class UpcomingSyncToSpringService (
    @Value("\${internal.token}") private val internalToken: String,
    @Value("\${spring.server.url}") private val springServerUrl: String,
    private val restTemplate: RestTemplate
) {
    fun sendToSpring(dtoList: List<UpcomingMovieDto>) {
        val url = "$springServerUrl/admin/movies/upcoming/sync"
        println("💥 전송 대상 URL: $url")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("x-internal-token", internalToken)
        }

        val request = HttpEntity(dtoList, headers)

        try {
            val response = restTemplate.exchange<String>(
                "http://localhost:8080/admin/movies/upcoming/sync",
                HttpMethod.POST,
                request
            )

            println("✅ 전송 성공! 상태 코드: ${response.statusCode}")
        } catch (e: Exception) {
            println("❌ Spring 서버 전송 실패: ${e.message}")
        }
    }
}
