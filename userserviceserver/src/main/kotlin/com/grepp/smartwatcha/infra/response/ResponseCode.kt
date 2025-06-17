package com.grepp.smartwatcha.infra.response

import org.springframework.http.HttpStatus

// 공통 API 응답 코드 정의용 enum 클래스
enum class ResponseCode(
    val code: String,
    val status: HttpStatus,
    val message: String
) {
    // 요청 성공
    OK("200", HttpStatus.OK, "정상적으로 완료되었습니다."),

    // 잘못된 요청 (유효성 실패 등)
    BAD_REQUEST("400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 인증 실패 또는 권한 없음
    UNAUTHORIZED("403", HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

    // 서버 내부 오류
    INTERNAL_SERVER_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "서버에러 입니다.");
}
