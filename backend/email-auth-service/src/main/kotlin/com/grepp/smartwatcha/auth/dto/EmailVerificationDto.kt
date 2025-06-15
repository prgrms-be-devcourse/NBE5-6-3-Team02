package com.grepp.smartwatcha.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendVerificationRequest(
    @field:NotBlank(message = "이메일은 필수 입력값입니다")
    @field:Email(message = "이메일 형식이 올바르지 않습니다")
    val email: String
)

data class VerifyCodeRequest(
    @field:NotBlank(message = "이메일은 필수 입력값입니다")
    @field:Email(message = "이메일 형식이 올바르지 않습니다")
    val email: String,

    @field:NotBlank(message = "인증 코드는 필수 입력값입니다")
    val code: String
)

data class VerificationResponse(
    val verified: Boolean,
    val message: String
)

data class CooldownResponse(
    val remainingSeconds: Long
) 