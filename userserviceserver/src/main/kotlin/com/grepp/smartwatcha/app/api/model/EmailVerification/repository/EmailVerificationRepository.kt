package com.grepp.smartwatcha.app.api.model.EmailVerification.repository

import com.grepp.smartwatcha.infra.entity.EmailVerification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailVerificationRepository : JpaRepository<EmailVerification, Long> {
    fun findByEmail(email: String): EmailVerification?
    fun findByEmailAndCode(email: String, code: String): EmailVerification?
} 