package com.grepp.smartwatcha.app.model.user.service;

import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.FindIdRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.ResetPasswordRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailVerificationRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailCodeVerifyRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.UserUpdateRequestDto;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.EmailVerificationJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.RatingJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.InterestJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.entity.EmailVerificationEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import com.grepp.smartwatcha.infra.jpa.entity.InterestEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserJpaService {
    private final UserJpaRepository userJpaRepository;
    private final EmailVerificationJpaRepository emailVerificationJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationJpaService emailVerificationJpaService;
    private final RatingJpaRepository ratingJpaRepository;
    private final InterestJpaRepository interestJpaRepository;

    @Transactional(transactionManager = "jpaTransactionManager")
    public Long signup(SignupRequestDto requestDto) {
        // 활성화된 계정 중에서만 이메일 중복 검사
        UserEntity existingUser = userJpaRepository.findByEmail(requestDto.getEmail())
                .filter(user -> user.getActivated())
                .orElse(null);
                
        if (existingUser != null) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        EmailVerificationEntity verification = emailVerificationJpaRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증이 필요합니다."));
        if (!verification.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }

        // 비활성화된 계정이 있는 경우 재사용
        UserEntity inactiveUser = userJpaRepository.findByEmail(requestDto.getEmail())
                .filter(user -> !user.getActivated())
                .orElse(null);

        UserEntity user;
        if (inactiveUser != null) {
            // 기존 비활성화 계정 재사용
            inactiveUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            inactiveUser.setName(requestDto.getName());
            inactiveUser.setPhoneNumber(requestDto.getPhoneNumber());
            inactiveUser.setActivated(true);
            user = inactiveUser;
        } else {
            // 새 계정 생성
            user = UserEntity.builder()
                    .email(requestDto.getEmail())
                    .password(passwordEncoder.encode(requestDto.getPassword()))
                    .name(requestDto.getName())
                    .phoneNumber(requestDto.getPhoneNumber())
                    .role(Role.USER)
                    .build();
        }
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

    public UserEntity findById(Long id) {
        return userJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(transactionManager = "jpaTransactionManager")
    public void updateUser(Long userId, UserUpdateRequestDto requestDto) {
        UserEntity user = findById(userId);

        // 비밀번호 변경이 요청된 경우
        if (requestDto.getNewPassword() != null && !requestDto.getNewPassword().isEmpty()) {
            // 현재 비밀번호 확인
            if (requestDto.getCurrentPassword() == null || requestDto.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
            }
            if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            // 새 비밀번호 확인
            if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
                throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
            }
            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        }

        // 이름과 전화번호 업데이트
        user.setName(requestDto.getName());
        user.setPhoneNumber(requestDto.getPhoneNumber());

        userJpaRepository.save(user);
    }

    @Transactional(transactionManager = "jpaTransactionManager")
    public void deleteUser(Long userId) {
        UserEntity user = findById(userId);
        user.unActivated();
        userJpaRepository.save(user);
    }

    public List<RatingEntity> findRatedMoviesByUserId(Long userId) {
        UserEntity user = findById(userId);
        return ratingJpaRepository.findByUser(user);
    }

    public List<InterestEntity> findWishlistMoviesByUserId(Long userId) {
        UserEntity user = findById(userId);
        return interestJpaRepository.findByUserAndStatus(user, Status.WATCH_LATER);
    }
} 