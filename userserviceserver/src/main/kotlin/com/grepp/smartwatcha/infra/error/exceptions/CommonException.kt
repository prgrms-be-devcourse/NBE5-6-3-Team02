package com.grepp.smartwatcha.infra.error.exceptions

import com.grepp.smartwatcha.infra.response.ResponseCode
import org.slf4j.LoggerFactory

// 공통 커스텀 예외 클래스
class CommonException(
    val code: ResponseCode,
    e: Exception? = null
) : RuntimeException(e) {

    // SLF4J Logger 를 통한 예외 로깅
    companion object {
        private val log = LoggerFactory.getLogger(CommonException::class.java)
    }

    // 예외가 존재하는 경우 에러 로그 출력
    init {
        e?.let { log.error(it.message, it) }
    }
}
