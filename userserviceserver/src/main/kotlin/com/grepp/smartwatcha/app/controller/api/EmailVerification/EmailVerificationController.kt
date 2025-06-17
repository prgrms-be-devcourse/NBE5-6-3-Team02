package com.grepp.smartwatcha.app.controller.api.EmailVerification

import com.grepp.smartwatcha.app.model.EmailVerification.dto.*
import com.grepp.smartwatcha.app.model.EmailVerification.service.EmailVerificationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/email-verification")
class EmailVerificationController(
    private val emailVerificationService: EmailVerificationService
) {
    /**
     * 인증 코드 전송 요청을 처리합니다.
     * 쿨다운 시간 내에는 에러 메시지를 반환합니다.
     */
    @PostMapping("/send")
    fun sendVerificationCode(@Valid @RequestBody request: SendVerificationRequest): ResponseEntity<VerificationResponse> {
        try {
            emailVerificationService.sendVerificationCode(request)
            return ResponseEntity.ok(VerificationResponse(verified = false, message = "인증 코드가 이메일로 전송되었습니다."))
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body(VerificationResponse(verified = false, message = e.message ?: "인증 코드 전송에 실패했습니다."))
        }
    }

    /**
     * 해당 이메일의 인증 코드 재전송까지 남은 쿨다운 시간을 조회합니다.
     */
    @GetMapping("/cooldown")
    fun getCooldownTime(@RequestParam email: String): ResponseEntity<CooldownResponse> {
        val remainingSeconds = emailVerificationService.getRemainingCooldownTime(email)
        return ResponseEntity.ok(CooldownResponse(remainingSeconds = remainingSeconds))
    }

    /**
     * 사용자가 입력한 인증 코드의 유효성을 검증합니다.
     */
    @PostMapping("/verify")
    fun verifyCode(@Valid @RequestBody request: VerifyCodeRequest): ResponseEntity<VerificationResponse> {
        val verified = emailVerificationService.verifyCode(request)
        return if (verified) {
            ResponseEntity.ok(VerificationResponse(verified = true, message = "이메일 인증이 완료되었습니다."))
        } else {
            ResponseEntity.badRequest().body(VerificationResponse(verified = false, message = "인증 코드가 유효하지 않거나 만료되었습니다."))
        }
    }
} 