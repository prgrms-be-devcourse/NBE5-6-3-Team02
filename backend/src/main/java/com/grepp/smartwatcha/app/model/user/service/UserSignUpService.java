package com.grepp.smartwatcha.app.model.user.service;

import com.grepp.smartwatcha.app.model.user.SignUpRequest;
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
public class UserSignUpService {
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationJpaRepository emailVerificationRepository;

    @Transactional
    public Long signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        var verification = emailVerificationRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증이 필요합니다."));
        if (!verification.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.USER)
                .build();
        return userRepository.save(user).getId();
    }
} 