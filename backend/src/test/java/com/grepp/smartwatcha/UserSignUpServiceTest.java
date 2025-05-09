package com.grepp.smartwatcha;

import com.grepp.smartwatcha.app.model.user.SignUpRequest;
import com.grepp.smartwatcha.app.model.user.repository.EmailVerificationJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.app.model.user.service.UserSignUpService;
import com.grepp.smartwatcha.infra.jpa.entity.EmailVerificationEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSignUpServiceTest {

    @Mock
    private UserJpaRepository userRepository;
    @Mock
    private EmailVerificationJpaRepository emailVerificationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserSignUpService userSignUpService;

    @Test
    @DisplayName("정상적으로 회원가입이 된다")
    void signUp_success() {
        // given
        SignUpRequest request = SignUpRequest.builder()
            .email("test@example.com")
            .password("password123")
            .name("테스터")
            .build();

        when(emailVerificationRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(EmailVerificationEntity.builder().verified(true).build()));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // when
        Long userId = userSignUpService.signUp(request);

        // then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("이메일이 중복되면 예외가 발생한다")
    void signUp_duplicateEmail() {
        // given
        SignUpRequest request = SignUpRequest.builder()
            .email("test@example.com")
            .password("password123")
            .name("테스터")
            .build();

        lenient().when(emailVerificationRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(EmailVerificationEntity.builder().verified(true).build()));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userSignUpService.signUp(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 사용 중인 이메일");
    }
}