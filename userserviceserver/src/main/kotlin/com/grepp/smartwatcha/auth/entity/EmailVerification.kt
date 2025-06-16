package com.grepp.smartwatcha.auth.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "email_verification")
data class EmailVerification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val code: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val expiredAt: LocalDateTime,

    @Column(nullable = false)
    val lastSentAt: LocalDateTime,

    @Column(nullable = false)
    var verified: Boolean = false
) 