package com.grepp.smartwatcha.app.model.EmailVerification.repository

import com.grepp.smartwatcha.infra.jpa.entity.EmailVerification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailVerificationRepository : JpaRepository<EmailVerification, Long> {
    fun findByEmail(email: String): EmailVerification?
    fun findByEmailAndCode(email: String, code: String): EmailVerification?
} 