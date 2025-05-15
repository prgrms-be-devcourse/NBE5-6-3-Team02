package com.grepp.smartwatcha.app.model.user.service;

import com.grepp.smartwatcha.app.model.user.dto.EmailVerificationRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailCodeVerifyRequestDto;
import com.grepp.smartwatcha.infra.jpa.entity.EmailVerificationEntity;
import com.grepp.smartwatcha.app.model.user.repository.EmailVerificationJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationJpaService {
    private final EmailVerificationJpaRepository emailVerificationRepository;
    private final UserJpaRepository userJpaRepository;
    private final JavaMailSender mailSender;

    @Value("${app.email.verification.expire-minutes:10}")
    private int expireMinutes;

    @Value("${app.email.verification.cooldown-seconds:60}")
    private int cooldownSeconds;

    public void sendVerificationCode(EmailVerificationRequestDto requestDto) {
        if (userJpaRepository.existsByEmailAndActivatedIsTrue(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // Check cooldown
        emailVerificationRepository.findByEmail(requestDto.getEmail())
            .ifPresent(entity -> {
                LocalDateTime now = LocalDateTime.now();
                long secondsSinceLastSent = ChronoUnit.SECONDS.between(entity.getLastSentAt(), now);
                if (secondsSinceLastSent < cooldownSeconds) {
                    throw new IllegalArgumentException(
                        String.format("인증 코드는 %d초 후에 다시 전송할 수 있습니다. (%d초 남음)", 
                            cooldownSeconds, cooldownSeconds - secondsSinceLastSent)
                    );
                }
            });

        String code = generateCode();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusMinutes(expireMinutes);
        
        emailVerificationRepository.findByEmail(requestDto.getEmail())
                .ifPresent(emailVerificationRepository::delete);
                
        EmailVerificationEntity entity = EmailVerificationEntity.builder()
                .email(requestDto.getEmail())
                .code(code)
                .createdAt(now)
                .expiredAt(expiredAt)
                .lastSentAt(now)
                .verified(false)
                .build();
        emailVerificationRepository.save(entity);
        sendEmail(requestDto.getEmail(), code);
    }

    public long getRemainingCooldownTime(String email) {
        return emailVerificationRepository.findByEmail(email)
            .map(entity -> {
                LocalDateTime now = LocalDateTime.now();
                long secondsSinceLastSent = ChronoUnit.SECONDS.between(entity.getLastSentAt(), now);
                return Math.max(0, cooldownSeconds - secondsSinceLastSent);
            })
            .orElse(0L);
    }

    public boolean verifyCode(EmailCodeVerifyRequestDto requestDto) {
        EmailVerificationEntity entity = emailVerificationRepository.findByEmailAndCode(requestDto.getEmail(), requestDto.getCode())
                .orElse(null);
        if (entity == null || entity.isVerified() || entity.getExpiredAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        entity.setVerified(true);
        emailVerificationRepository.save(entity);
        return true;
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[스마트왓챠] 이메일 인증 코드 안내");
        message.setText("인증 코드: " + code + "\n10분 이내에 입력해 주세요.");
        mailSender.send(message);
    }
} 