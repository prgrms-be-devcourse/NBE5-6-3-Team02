package com.grepp.smartwatcha.app.model.upcomingmovie.service

import com.grepp.smartwatcha.app.controller.api.upcomingmovie.api.*
import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.*
import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// TMDB 공개 예정작 영화 조회 및 상세 정보 병합 서비스
// - TMDB API로부터 공개 예정작 목록을 조회하고, 각 영화에 대한 상세 정보를 병렬로 조회하여 DTO에 보강한다.
// - 주요 보강 항목: 크레딧(배우/감독/작가), 등급/개봉유형, 제작국가
@Service
class UpcomingMovieFetchService(
    private val upcomingMovieApi: UpcomingMovieApi,
    private val creditApi: UpcomingMovieCreditApi,
    private val releaseDateApi: UpcomingMovieReleaseDateApi,
    private val detailApi: UpcomingMovieDetailApi,
    private val dtoEnricher: UpcomingMovieDetailEnricher
) {
    @Value("\${tmdb.api.key}")
    private lateinit var apiKey: String

    private val log = LoggerFactory.getLogger(UpcomingMovieFetchService::class.java)

    // TMDB로부터 전체 공개 예정작 목록을 페이징 방식으로 조회
    // - 날짜가 오늘 이후인 영화만 필터링하여 반환
    // - 각 페이지를 순차적으로 호출하여 누적
    fun fetchUpcomingMovies(): List<UpcomingMovieDto> {
        val page = 1
        val response: UpcomingMovieApiResponse =
            upcomingMovieApi.getUpcomingMovies(apiKey, "en-US", page, "US")
        val totalPages = response.totalPages

        val allMovies = response.movies.toMutableList()
        for (p in 2..totalPages) {
            val nextPage = upcomingMovieApi.getUpcomingMovies(apiKey, "en-US", p, "US")
            allMovies.addAll(nextPage.movies)
        }

        // 오늘 이후 개봉하는 영화만 필터링
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val upcomingOnly = allMovies.filter { dto ->
            try {
                val parsedDate = LocalDate.parse(dto.releaseDate, formatter)
                parsedDate.isAfter(today)
            } catch (e: Exception) {
                false // 잘못된 날짜 형식은 필터링 제외
            }
        }

        log.info("🎯 TMDB에서 받은 영화 ${allMovies.size}건 중, 개봉 예정 ${upcomingOnly.size}건 필터링")

        return upcomingOnly
    }

    // 단일 영화 DTO에 대한 상세 정보 보강 (크레딧, 등급, 국가)
    // - 코루틴 기반으로 병렬 fetch → DTO 보강 수행
    suspend fun buildEnrichedDto(baseDto: UpcomingMovieDto, apiKey: String): UpcomingMovieDto? {
        val movieId = baseDto.id

        return withContext(Dispatchers.IO) {
            val (credits, releaseDates, details) = fetchAll(movieId, apiKey)

            credits?.let { dtoEnricher.enrichCredits(baseDto, it) }
            releaseDates?.let { dtoEnricher.enrichCertification(baseDto, it) }
            details?.let { dtoEnricher.enrichCountry(baseDto, it) }

            baseDto
        }
    }

    // 단일 영화에 대한 3종 API 병렬 호출 (크레딧, 릴리즈, 디테일)
    // - 예외 발생 시 로그만 남기고 null 처리
    private suspend fun fetchAll(
        movieId: Long,
        apiKey: String
    ): Triple<UpcomingMovieCreditApiResponse?, UpcomingMovieReleaseDateApiResponse?, UpcomingMovieDetailApiResponse?> =
        coroutineScope {
            val creditDeferred = async {
                try {
                    creditApi.getCredits(movieId, apiKey)
                } catch (e: Exception) {
                    log.error("❌ [fetchCredits] 실패 - movieId: {}", movieId, e)
                    null
                }
            }

            val releaseDeferred = async {
                try {
                    releaseDateApi.getReleaseDates(movieId, apiKey)
                } catch (e: Exception) {
                    log.error("❌ [fetchReleaseDates] 실패 - movieId: {}", movieId, e)
                    null
                }
            }

            val detailDeferred = async {
                try {
                    detailApi.getDetails(movieId, apiKey)
                } catch (e: Exception) {
                    log.error("❌ [fetchDetails] 실패 - movieId: {}", movieId, e)
                    null
                }
            }

            Triple(creditDeferred.await(), releaseDeferred.await(), detailDeferred.await())
        }
}
