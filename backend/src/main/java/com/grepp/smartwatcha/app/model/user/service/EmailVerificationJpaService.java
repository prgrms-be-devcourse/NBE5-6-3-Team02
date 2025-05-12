package com.grepp.smartwatcha.app.model.user.service;

import com.grepp.smartwatcha.app.model.user.dto.EmailVerificationRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailCodeVerifyRequestDto;
import com.grepp.smartwatcha.infra.jpa.entity.EmailVerificationEntity;
import com.grepp.smartwatcha.app.model.user.repository.EmailVerificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationJpaService {
    private final EmailVerificationJpaRepository emailVerificationRepository;
    private final JavaMailSender mailSender;

    @Value("${app.email.verification.expire-minutes:10}")
    private int expireMinutes;

    public void sendVerificationCode(EmailVerificationRequestDto requestDto) {
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
                .verified(false)
                .build();
        emailVerificationRepository.save(entity);
        sendEmail(requestDto.getEmail(), code);
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