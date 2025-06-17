package com.grepp.smartwatcha.infra.response

// 공통 API 응답 포맷 클래스
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T?
) {
    companion object {

        // 성공 응답 생성 (데이터 포함)
        @JvmStatic
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(ResponseCode.OK.code, ResponseCode.OK.message, data)
        }

        // 성공 응답 생성 (데이터 없음)
        @JvmStatic
        fun <T> noContent(): ApiResponse<T> {
            return ApiResponse(ResponseCode.OK.code, ResponseCode.OK.message, null)
        }

        // 에러 응답 생성 (데이터 없음)
        @JvmStatic
        fun <T> error(code: ResponseCode): ApiResponse<T> {
            return ApiResponse(code.code, code.message, null)
        }

        // 에러 응답 생성 (데이터 포함)
        @JvmStatic
        fun <T> error(code: ResponseCode, data: T): ApiResponse<T> {
            return ApiResponse(code.code, code.message, data)
        }
    }
}
