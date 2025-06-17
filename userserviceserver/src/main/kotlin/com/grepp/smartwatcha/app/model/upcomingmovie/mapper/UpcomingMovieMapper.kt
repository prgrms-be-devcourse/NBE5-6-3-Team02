package com.grepp.smartwatcha.app.model.upcomingmovie.mapper

import com.grepp.smartwatcha.app.model.upcomingmovie.dto.UpcomingMovieDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

// 공개 예정 영화 DTO 변환 및 Neo4j 저장용 파라미터 매핑 담당 클래스
// - 날짜 문자열 파싱
// - DTO 내 리스트 필드 정제 후 파라미터 맵 생성
@Component
class UpcomingMovieMapper {

    private val log = LoggerFactory.getLogger(UpcomingMovieMapper::class.java)

    // 날짜 문자열을 LocalDateTime(자정 기준)으로 변환
    fun parseDateWithDefaultTime(dateStr: String?, title: String): LocalDateTime? {
        if (dateStr.isNullOrBlank()) {
            log.warn("📅 releaseDate 가 비어 있어 LocalDateTime 으로 변환할 수 없습니다. [title: {}]", title)
            return null
        }
        return try {
            LocalDate.parse(dateStr).atStartOfDay()
        } catch (e: Exception) {
            log.warn("📅 releaseDate 파싱 실패: '{}' [title: {}]", dateStr, title)
            null
        }
    }

    // DTO 내 필드들을 Neo4j Cypher 쿼리용 파라미터 Map 으로 변환
    // - actorNames, directorNames, writerNames: null/공백 제거 및 중복 제거
    // - genreIds: null 제거 및 중복 제거
    fun toParameters(dto: UpcomingMovieDto): Map<String, Any> {
        val actorNames = dto.actorNames
            .filterNotNull()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        val directorNames = dto.directorNames
            .filterNotNull()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        val writerNames = dto.writerNames
            .filterNotNull()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        val genreIds = dto.genreIds
            ?.filterNotNull()
            ?.distinct()
            ?: emptyList()

        return mapOf(
            "id" to dto.id,
            "title" to dto.title,
            "actorNames" to actorNames,
            "directorNames" to directorNames,
            "writerNames" to writerNames,
            "genreIds" to genreIds
        )
    }
}
