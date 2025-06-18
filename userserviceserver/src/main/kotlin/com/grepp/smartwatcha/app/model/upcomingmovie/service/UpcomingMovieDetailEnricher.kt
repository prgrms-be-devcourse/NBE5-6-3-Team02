package com.grepp.smartwatcha.app.model.upcomingmovie.service

import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieCreditApiResponse
import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieDetailApiResponse
import com.grepp.smartwatcha.app.controller.api.upcomingmovie.payload.UpcomingMovieReleaseDateApiResponse
import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

// 공개 예정작 영화 DTO 데이터 보강 서비스 - TMDB API 에서 조회한 상세 정보를 DTO 에 추가하는 역할
// 각 보강 메서드는 독립적으로 동작하며, 일부 정보 조회 실패 시에도 다른 정보는 보강 가능

// 주요 기능:
//  - 영화 크레딧 정보(배우, 감독, 작가) 보강
//  - 영화 등급 및 개봉 유형 정보 보강
//  - 영화 제작 국가 정보 보강
@Component
open class UpcomingMovieDetailEnricher {
    private val log = LoggerFactory.getLogger(javaClass)

    // 영화의 크레딧 정보 보강 (배우, 감독, 작가)
    fun enrichCredits(dto: UpcomingMovieDto, credits: UpcomingMovieCreditApiResponse?) {
        try {
            if (credits == null) {
                log.warn("⚠️ [enrichCredits] 응답이 null 입니다. 영화 ID: {}", dto.id)
                return
            }
            // 배우
            val actorNames = credits.cast
                ?.sortedBy { it.order }
                ?.take(5)
                ?.mapNotNull { it.name } // name도 nullable이니까 안전하게
                ?: emptyList()

            // 감독
            val directorNames = credits.crew
                ?.filter { it.job.equals("Director", ignoreCase = true) }
                ?.mapNotNull { it.name }
                ?.distinct()
                ?: emptyList()

            // 작가
            val writerNames = credits.crew
                ?.filter {
                    it.job.equals("Writer", ignoreCase = true) ||
                            it.job.equals("Screenplay", ignoreCase = true)
                }
                ?.mapNotNull { it.name }
                ?.distinct()
                ?: emptyList()

            dto.actorNames = actorNames
            dto.directorNames = directorNames
            dto.writerNames = writerNames
        } catch (e: Exception) {
            log.warn(
                "⚠️ [enrichCredits] 실패 - 영화 ID: {}, 제목: {}, 사유: {}",
                dto.id,
                dto.title,
                e.message
            )
        }
    }

    // 영화의 등급 및 개봉 유형 정보를 DTO 에 보강
    // - 미국 기준 [등급 정보 & 개봉 유형] 추가
    fun enrichCertification(
        dto: UpcomingMovieDto,
        releaseData: UpcomingMovieReleaseDateApiResponse
    ) {
        try {
            val usRelease = releaseData.results
                ?.firstOrNull { it.iso_3166_1 == "US" }

            if (usRelease?.release_dates.isNullOrEmpty()) {
                log.warn("⚠️ [enrichCertification] 미국 release_dates 없음 - 영화 ID: {}", dto.id)
                return
            }

            val sortedReleases = usRelease?.release_dates?.sortedBy { it.releaseDate } ?: emptyList()

            // releaseType 설정
            sortedReleases
                .firstOrNull { it.type != null }
                ?.let { dto.releaseType = it.type }

            // certification 설정
            sortedReleases
                .firstOrNull { !it.certification.isNullOrBlank() }
                ?.let { dto.certification = it.certification }

        } catch (e: Exception) {
            log.warn(
                "⚠️ [enrichCertification] 실패 - 영화 ID: {}, 제목: {}, 사유: {}",
                dto.id,
                dto.title,
                e.message
            )
        }
    }

    // 영화의 제작 국가 정보를 DTO 에 보강
    // - 첫 번째 제작 국가의 ISO 코드 추가
    fun enrichCountry(dto: UpcomingMovieDto, detail: UpcomingMovieDetailApiResponse?) {
        try {
            val countryCode = detail?.productionCountries?.firstOrNull()?.iso31661
            if (countryCode != null) {
                dto.country = countryCode
            }
        } catch (e: Exception) {
            log.warn(
                "⚠️ [enrichCountry] 실패 - 영화 ID: {}, 제목: {}, 사유: {}",
                dto.id,
                dto.title,
                e.message
            )
        }
    }
}
