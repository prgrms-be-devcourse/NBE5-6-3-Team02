package com.grepp.smartwatcha.app.controller.api.upcomingmovie

import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto
import com.grepp.smartwatcha.app.model.upcomingmovie.service.UpcomingMovieSyncService
import com.grepp.smartwatcha.app.model.upcomingmovie.service.UpcomingSyncToSpringService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/movies/upcoming")
class UpcomingSyncApiController(
    private val syncService: UpcomingMovieSyncService,
    private val syncToSpringService: UpcomingSyncToSpringService
) {

    @Value("\${internal.token}")
    private lateinit var internalToken: String

    private val log = LoggerFactory.getLogger(UpcomingSyncApiController::class.java)

    // Kotlin → Spring 서버로 enrich된 DTO 전송용 (Spring 서버가 JSON List로 받음)
    @PostMapping("/sync")
    fun syncUpcomingMovies(
        @RequestHeader("x-internal-token", required = false) token: String?
    ): ResponseEntity<List<UpcomingMovieDto>> {
        if (token != internalToken) {
            log.warn("❌ 인증 실패: 토큰 불일치")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val result: List<UpcomingMovieDto> = runBlocking {
            syncService.syncAllAndReturnDtoList()
        }

        log.info("✅ Kotlin 서버에서 공개 예정작 동기화 완료 (총 ${result.size}건)")
        return ResponseEntity.ok(result)
    }

    // enrich 완료 후 Spring 서버에 전송 (RestTemplate 기반 호출)
    @GetMapping("/sync-and-send")
    suspend fun syncAndSendToSpring(): ResponseEntity<String> {
        val result = syncService.syncAll()
        syncToSpringService.sendToSpring(result.dtoList)
        return ResponseEntity.ok("✅ enrich + Spring 전송 완료")
    }
}
