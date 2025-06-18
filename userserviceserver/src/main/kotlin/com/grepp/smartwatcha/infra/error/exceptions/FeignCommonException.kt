package com.grepp.smartwatcha.infra.error.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

// Feign 호출 중 발생한 예외를 공통적으로 처리하기 위한 커스텀 예외 클래스
class FeignCommonException : RuntimeException {
    val code: String? // 응답 코드 (예: "FEIGN_TIMEOUT", "FEIGN_500" 등)
    override val message: String? // 예외 메시지
    val status: HttpStatus? // HTTP 상태 코드

    // 기본 생성자
    constructor() : super() {
        this.code = null
        this.message = null
        this.status = null
    }

    // 예외 원인만 전달하는 생성자
    constructor(cause: Throwable) : super(cause) {
        this.code = null
        this.message = null
        this.status = null
    }

    // 코드, 메시지, 상태 코드를 명시적으로 전달하는 생성자
    constructor(code: String, message: String, status: HttpStatus) : super(message) {
        this.code = code
        this.message = message
        this.status = status
    }

    // 예외 원인과 함께 상세 정보를 전달하는 생성자
    constructor(cause: Throwable, code: String, message: String, status: HttpStatus) : super(message, cause) {
        this.code = code
        this.message = message
        this.status = status
    }

    companion object {
        // 예외 발생 시 로깅에 사용할 Logger
        private val log = LoggerFactory.getLogger(FeignCommonException::class.java)
    }

    // toString 오버라이드: 예외 정보 출력용 문자열
    override fun toString(): String {
        return "FeignCommonException(code=$code, message=$message, status=$status)"
    }
}
