package com.grepp.smartwatcha.infra.response

import org.springframework.data.domain.Page

// 페이지네이션 응답을 위한 래퍼 클래스
class PageResponse<T>(
    private val url: String,
    private val page: Page<T>,
    private val pageButtonCnt: Int
) {
    
    fun url(): String = url // 요청 URL 반환

    // 현재 페이지 번호 (1부터 시작)
    fun currentNumber(): Int = page.number + 1

    // 이전 페이지 번호 (최소값은 1)
    fun prevPage(): Int = maxOf(currentNumber() - 1, 1)

    // 다음 페이지 번호 (최대값은 전체 페이지 수)
    fun nextPage(): Int = minOf(currentNumber() + 1, calcTotalPage())

    // 시작 페이지 번호
    fun startNumber(): Int = (page.number / pageButtonCnt) * pageButtonCnt + 1

    // 끝 페이지 번호

    fun endNumber(): Int = minOf(startNumber() + pageButtonCnt - 1, calcTotalPage())

    // 현재 페이지의 콘텐츠 리스트
    fun content(): List<T> = page.content

    // 전체 페이지 수
    fun totalPages(): Int = calcTotalPage()

    // 총 페이지 수 계산
    private fun calcTotalPage(): Int {
        val totalPage = page.totalPages
        return if (totalPage == 0) 1 else totalPage
    }
}
