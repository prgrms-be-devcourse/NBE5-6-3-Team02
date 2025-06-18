package com.grepp.smartwatcha.infra.error

import com.grepp.smartwatcha.infra.error.exceptions.CommonException
import com.grepp.smartwatcha.infra.response.ApiResponse
import com.grepp.smartwatcha.infra.response.ResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// REST API 전용 전역 예외 처리 핸들러
@RestControllerAdvice(basePackages = ["com.grepp.spring.app.controller.api"])
class RestApiExceptionAdvice {

    private val log = LoggerFactory.getLogger(RestApiExceptionAdvice::class.java)

    // @Valid 유효성 검사 실패 예외 처리 핸들러
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validatorHandler(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        log.info(ex.message, ex)

        val errors = linkedMapOf<String, String>()
        ex.fieldErrors.forEach { e -> errors[e.field] = e.defaultMessage ?: "Invalid value" }

        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(ResponseCode.BAD_REQUEST, errors))
    }

    // 지원하지 않는 HTTP 메서드 호출 시 처리 핸들러
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotSupportedHandler(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(ResponseCode.BAD_REQUEST, ex.message ?: "Unsupported method"))
    }

    // 공통 커스텀 예외(CommonException) 처리 핸들러
    @ExceptionHandler(CommonException::class)
    fun restApiExceptionHandler(ex: CommonException): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity
            .status(ex.code.status)
            .body(ApiResponse.error(ex.code))
    }

    // 인증 실패 또는 권한 거부 예외 처리 핸들러
    // @ExceptionHandler(AuthorizationDeniedException::class)
    // fun authorizationDeniedHandler(ex: AuthorizationDeniedException): ResponseEntity<ApiResponse<String>> {
    //     return ResponseEntity
    //         .status(HttpStatus.UNAUTHORIZED)
    //         .body(ApiResponse.error(ResponseCode.UNAUTHORIZED))
    // }

    // 그 외 모든 런타임 예외 처리 핸들러
    @ExceptionHandler(RuntimeException::class)
    fun runtimeExceptionHandler(ex: RuntimeException): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR))
    }
}
