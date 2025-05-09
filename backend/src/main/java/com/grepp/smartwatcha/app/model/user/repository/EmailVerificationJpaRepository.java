package com.grepp.smartwatcha.app.model.user.repository;

import com.grepp.smartwatcha.infra.jpa.entity.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerificationEntity, Long> {
    Optional<EmailVerificationEntity> findByEmail(String email);
    Optional<EmailVerificationEntity> findByEmailAndCode(String email, String code);
} 