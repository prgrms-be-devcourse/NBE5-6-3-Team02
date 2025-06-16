package com.grepp.smartwatcha.app.api.model.EmailVerification.service

import com.grepp.smartwatcha.app.api.model.EmailVerification.dto.SendVerificationRequest
import com.grepp.smartwatcha.app.api.model.EmailVerification.dto.VerifyCodeRequest
import com.grepp.smartwatcha.infra.entity.EmailVerification
import com.grepp.smartwatcha.app.api.model.EmailVerification.repository.EmailVerificationRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

// 이메일 인증 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
class EmailVerificationService(
    private val emailVerificationRepository: EmailVerificationRepository,
    private val mailSender: JavaMailSender
) {
    @Value("\${app.email.verification.expire-minutes:1}")
    private var expireMinutes: Int = 1

    @Value("\${app.email.verification.cooldown-seconds:60}")
    private var cooldownSeconds: Int = 60

    /**
     * 인증 코드를 생성하여 이메일로 전송
     * 쿨다운 시간 내에는 재전송이 제한
     * 기존 인증 정보가 있으면 삭제 후 새로 저장
     */
    @Transactional
    fun sendVerificationCode(request: SendVerificationRequest) {
        // Check cooldown
        emailVerificationRepository.findByEmail(request.email)?.let { entity ->
            val now = LocalDateTime.now()
            val secondsSinceLastSent = ChronoUnit.SECONDS.between(entity.lastSentAt, now)
            if (secondsSinceLastSent < cooldownSeconds) {
                throw IllegalArgumentException(
                    "인증 코드는 ${cooldownSeconds}초 후에 다시 전송할 수 있습니다. (${cooldownSeconds - secondsSinceLastSent}초 남음)"
                )
            }
        }

        val code = generateCode()
        val now = LocalDateTime.now()
        val expiredAt = now.plusMinutes(expireMinutes.toLong())

        emailVerificationRepository.findByEmail(request.email)?.let {
            emailVerificationRepository.delete(it)
        }

        val entity = EmailVerification(
            email = request.email,
            code = code,
            createdAt = now,
            expiredAt = expiredAt,
            lastSentAt = now,
            verified = false
        )
        emailVerificationRepository.save(entity)
        sendEmail(request.email, code)
    }

    /**
     * 해당 이메일의 인증 코드 재전송까지 남은 쿨다운 시간을 초 단위로 반환
     */
    fun getRemainingCooldownTime(email: String): Long {
        return emailVerificationRepository.findByEmail(email)?.let { entity ->
            val now = LocalDateTime.now()
            val secondsSinceLastSent = ChronoUnit.SECONDS.between(entity.lastSentAt, now)
            maxOf(0, cooldownSeconds - secondsSinceLastSent)
        } ?: 0L
    }

    /**
     * 사용자가 입력한 인증 코드가 유효한지 검증
     * 인증 성공 시 verified 상태로 변경
     */
    @Transactional
    fun verifyCode(request: VerifyCodeRequest): Boolean {
        val entity = emailVerificationRepository.findByEmailAndCode(request.email, request.code)
            ?: return false

        if (entity.verified || entity.expiredAt.isBefore(LocalDateTime.now())) {
            return false
        }

        entity.verified = true
        emailVerificationRepository.save(entity)
        return true
    }

    /**
     * 6자리 숫자 인증 코드를 랜덤으로 생성
     */
    private fun generateCode(): String {
        return (100000 + Random.nextInt(900000)).toString()
    }

    /**
     * 인증 코드를 이메일로 발송
     */
    private fun sendEmail(to: String, code: String) {
        val message = SimpleMailMessage().apply {
            setTo(to)
            subject = "[SmartWatcha] Email Verification Code"
            text = "Your verification code: $code\nPlease enter within 1 minute."
        }
        mailSender.send(message)
    }
} 