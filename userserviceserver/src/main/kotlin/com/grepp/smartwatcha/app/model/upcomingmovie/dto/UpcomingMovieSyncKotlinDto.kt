package com.grepp.smartwatcha.app.model.upcomingmovie.dto

// 코틀린에서 Java로 전달할 공개 예정작 동기화 결과 DTO
// - Java 와 이름 충돌 방지를 위해 이름은 UpcomingMovieSyncKotlinDto 로 설정
data class UpcomingMovieSyncKotlinDto(
    val total: Int,
    val success: Int,
    val failed: Int,
    val skipped: Int,
    val enrichFailed: Int,
    val skippedIds: List<Long>,
    val skippedReasons: List<String>,
    val failedIds: List<Long>,
    val dtoList: List<UpcomingMovieDto> // 추가
)
