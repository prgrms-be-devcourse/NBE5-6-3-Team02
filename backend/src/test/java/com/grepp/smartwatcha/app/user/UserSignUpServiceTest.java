package com.grepp.smartwatcha.app.user;

import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.app.model.user.repository.EmailVerificationJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.app.model.user.service.UserJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.EmailVerificationEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
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
    private UserJpaService userJpaService;

    @Test
    @DisplayName("정상적으로 회원가입이 된다")
    void signup_success() {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .name("테스터")
            .phoneNumber("01012345678")
            .build();

        when(emailVerificationRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(EmailVerificationEntity.builder().verified(true).build()));
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // when
        Long userId = userJpaService.signup(requestDto);

        // then
        assertThat(userId).isEqualTo(1L);
        verify(userRepository).save(argThat(user ->
            user.getEmail().equals("test@example.com") &&
                user.getPassword().equals("encodedPassword") &&
                user.getName().equals("테스터") &&
                user.getPhoneNumber().equals("01012345678") &&
                user.getRole().equals(Role.USER) &&
                user.getActivated()
        ));
    }

    @Test
    @DisplayName("활성화된 계정의 이메일로 가입하면 예외가 발생한다")
    void signUp_duplicateActiveEmail() {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .name("테스터")
            .phoneNumber("01012345678")
            .build();

        UserEntity existingUser = UserEntity.builder()
            .email("test@example.com")
            .password("oldPassword")
            .name("기존사용자")
            .phoneNumber("01087654321")
            .role(Role.USER)
            .build();
        existingUser.setActivated(true);

        when(emailVerificationRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(EmailVerificationEntity.builder().verified(true).build()));
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> userJpaService.signup(requestDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 사용 중인 이메일");
    }

    @Test
    @DisplayName("비활성화된 계정의 이메일로 가입하면 해당 계정이 재활성화된다")
    void signUp_reactivateInactiveAccount() {
        // given
        SignupRequestDto requestDto = SignupRequestDto.builder()
            .email("test@example.com")
            .password("newPassword123")
            .name("새사용자")
            .phoneNumber("01012345678")
            .build();

        UserEntity inactiveUser = UserEntity.builder()
            .email("test@example.com")
            .password("oldPassword")
            .name("이전사용자")
            .phoneNumber("01087654321")
            .role(Role.USER)
            .build();
        inactiveUser.setActivated(false);
        inactiveUser.setId(1L);

        when(emailVerificationRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(EmailVerificationEntity.builder().verified(true).build()));
        when(userRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(inactiveUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(inactiveUser);

        // when
        Long userId = userJpaService.signup(requestDto);

        // then
        assertThat(userId).isEqualTo(1L);
        verify(userRepository).save(argThat(user ->
            user.getId().equals(1L) &&
                user.getEmail().equals("test@example.com") &&
                user.getPassword().equals("newEncodedPassword") &&
                user.getName().equals("새사용자") &&
                user.getPhoneNumber().equals("01012345678") &&
                user.getRole().equals(Role.USER) &&
                user.getActivated()
        ));
    }
}