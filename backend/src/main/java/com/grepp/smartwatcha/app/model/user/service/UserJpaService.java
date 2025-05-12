package com.grepp.smartwatcha.app.model.user.service;

import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import com.grepp.smartwatcha.app.model.user.repository.EmailVerificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserJpaService {
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationJpaRepository emailVerificationRepository;

    @Transactional(transactionManager = "jpaTransactionManager")
    public Long signup(SignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        var verification = emailVerificationRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증이 필요합니다."));
        if (!verification.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        UserEntity user = UserEntity.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .role(Role.USER)
                .build();
        return userRepository.save(user).getId();
    }
} 