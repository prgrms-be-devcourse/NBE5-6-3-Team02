package com.grepp.smartwatcha.app.model.upcomingmovie.service

import com.grepp.smartwatcha.app.model.upcomingmovie.UpcomingMovieSender
import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto
import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieSyncKotlinDto
import com.grepp.smartwatcha.app.model.upcomingmovie.mapper.UpcomingMovieMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class UpcomingMovieSyncService(
    private val fetchService: UpcomingMovieFetchService,
    private val mapper: UpcomingMovieMapper,
    private val sender: UpcomingMovieSender
) {
    @Value("\${tmdb.api.key}")
    private lateinit var apiKey: String

    private val log = LoggerFactory.getLogger(UpcomingMovieSyncService::class.java)

    @Scheduled(cron = "0 0 0 ? * MON") // 매주 월요일 00:00
    fun syncAllUpcomingMovies() {
        runBlocking {
            val elapsed = measureTimeMillis {
                val result = syncAll()

                if (result.dtoList.isNotEmpty()) {
                    val success = sender.sendToSpring(result.dtoList)
                    if (success) {
                        log.info("✅ Kotlin → Spring 동기화 성공 (전송: ${result.dtoList.size}건)")
                    } else {
                        log.warn("❌ Kotlin → Spring 동기화 실패")
                    }
                } else {
                    log.warn("⚠️ enrich된 영화가 없어 전송 생략")
                }
            }

            log.info("⏱️ 전체 enrich + 전송 시간: ${elapsed}ms")
        }
    }

    suspend fun syncAll(): UpcomingMovieSyncKotlinDto {
        log.info("🕒 [공개 예정작] 동기화 시작")

        val allMovies = fetchService.fetchUpcomingMovies()
        val total = allMovies.size

        var enrichTimeMs: Long = 0L

        val enrichedDtos = mutableListOf<UpcomingMovieDto>()
        val enrichFailedIds = mutableListOf<Long>()
        val enrichFailedReasons = mutableListOf<String>()

        val skippedDtos = mutableListOf<UpcomingMovieDto>()
        val skippedIds = mutableListOf<Long>()
        val skippedReasons = mutableListOf<String>()

        enrichTimeMs = measureTimeMillis {
            val enrichedResults = coroutineScope {
                allMovies.map { baseDto ->
                    async {
                        try {
                            val enriched = fetchService.buildEnrichedDto(baseDto, apiKey)
                            Triple(baseDto, enriched, null)
                        } catch (e: Exception) {
                            Triple(baseDto, null, e)
                        }
                    }
                }.awaitAll()
            }

            for ((baseDto, enrichedDto, exception) in enrichedResults) {
                if (exception != null || enrichedDto == null) {
                    enrichFailedIds += baseDto.id
                    enrichFailedReasons += "enrich 실패: ${exception?.message ?: "결과 없음"}"
                    continue
                }

                // ✅ enrich 성공했지만 스킵 조건
                when {
                    enrichedDto.title.isBlank() -> {
                        skippedDtos += enrichedDto
                        skippedIds += baseDto.id
                        skippedReasons += "제목 없음"
                        continue
                    }
                    enrichedDto.releaseDate.isNullOrBlank() -> {
                        skippedDtos += enrichedDto
                        skippedIds += baseDto.id
                        skippedReasons += "개봉일 없음"
                        continue
                    }
                    enrichedDto.certification.isNullOrBlank() -> {
                        skippedDtos += enrichedDto
                        skippedIds += baseDto.id
                        skippedReasons += "등급 정보 없음 (certification)"
                        continue
                    }
                    enrichedDto.country.isNullOrBlank() -> {
                        skippedDtos += enrichedDto
                        skippedIds += baseDto.id
                        skippedReasons += "국가 정보 없음 (country)"
                        continue
                    }
                }

                enrichedDtos += enrichedDto
            }
        }

        // ✅ 요약 로그
        log.info("=========================================================================")
        log.info("📊 [Kotlin enrich 요약]")
        log.info("✅ enrich + 저장 대상: ${enrichedDtos.size}건")
        log.info("⏭️ enrich 후 스킵된 항목: ${skippedDtos.size}건")
        log.info("⚠️ enrich 실패: ${enrichFailedIds.size}건")
        log.info("🎬 총 TMDB 원본 영화 수: $total 건")
        log.info("=========================================================================")

        if (skippedIds.isNotEmpty()) {
            log.info("⏭️ [스킵된 영화 목록]")
            for (i in skippedIds.indices) {
                log.info("   - [${skippedIds[i]}] 사유: ${skippedReasons[i]}")
            }
            log.info("=========================================================================")
        }

        if (enrichFailedIds.isNotEmpty()) {
            log.info("❌ [enrich 실패 목록]")
            for (i in enrichFailedIds.indices) {
                log.info("   - [${enrichFailedIds[i]}] 사유: ${enrichFailedReasons[i]}")
            }
            log.info("=========================================================================")
        }

        log.info("⏱️ enrich 병렬 처리 시간: ${enrichTimeMs}ms")

        return UpcomingMovieSyncKotlinDto(
            total = total,
            success = enrichedDtos.size,
            failed = 0,
            skipped = skippedDtos.size,
            enrichFailed = enrichFailedIds.size,
            skippedIds = skippedIds,
            skippedReasons = skippedReasons,
            failedIds = enrichFailedIds,
            dtoList = enrichedDtos
        )
    }

    suspend fun syncAllAndReturnDtoList(): List<UpcomingMovieDto> {
        log.info("📤 Spring 서버로 enrich된 공개 예정작 리스트 전달 요청 수신")
        val result = syncAll()
        return result.dtoList
    }
}
