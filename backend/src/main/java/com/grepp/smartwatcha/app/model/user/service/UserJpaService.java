package com.grepp.smartwatcha.app.model.user.service;

import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.FindIdRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.ResetPasswordRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailVerificationRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailCodeVerifyRequestDto;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.EmailVerificationJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.entity.EmailVerificationEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserJpaService {
    private final UserJpaRepository userJpaRepository;
    private final EmailVerificationJpaRepository emailVerificationJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationJpaService emailVerificationJpaService;

    @Transactional(transactionManager = "jpaTransactionManager")
    public Long signup(SignupRequestDto requestDto) {
        if (userJpaRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        EmailVerificationEntity verification = emailVerificationJpaRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증이 필요합니다."));
        if (!verification.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        UserEntity user = UserEntity.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .role(Role.USER)
                .build();
        return userJpaRepository.save(user).getId();
    }

    public String findIdByName(FindIdRequestDto findIdRequestDto) {
        return userJpaRepository.findByNameAndPhoneNumber(findIdRequestDto.getName(), findIdRequestDto.getPhoneNumber())
            .map(UserEntity::getEmail)
            .orElseThrow(() -> new IllegalArgumentException("해당 정보로 등록된 사용자가 없습니다."));
    }

    public void sendPasswordResetCode(String email) {
        UserEntity user = userJpaRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));
        
        emailVerificationJpaService.sendVerificationCode(
            new EmailVerificationRequestDto(email)
        );
    }

    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        // 비밀번호 확인
        if (!resetPasswordRequestDto.getNewPassword().equals(resetPasswordRequestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 인증 코드 확인
        boolean verified = emailVerificationJpaService.verifyCode(
            new EmailCodeVerifyRequestDto(
                resetPasswordRequestDto.getEmail(),
                resetPasswordRequestDto.getVerificationCode()
            )
        );

        if (!verified) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않거나 만료되었습니다.");
        }

        // 비밀번호 변경
        UserEntity user = userJpaRepository.findByEmail(resetPasswordRequestDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userJpaRepository.save(user);
    }
} 