package com.grepp.smartwatcha.app.model.user.service;

import com.grepp.smartwatcha.app.model.user.dto.RatedMovieDto;
import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.FindIdRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.ResetPasswordRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.UserInfoDto;
import com.grepp.smartwatcha.app.model.user.dto.UserUpdateRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.WishlistMovieDto;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.RatingJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.InterestJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import com.grepp.smartwatcha.infra.jpa.entity.InterestEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class UserJpaService {
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final RatingJpaRepository ratingJpaRepository;
    private final InterestJpaRepository interestJpaRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String emailAuthApiBaseUrl = "http://localhost:8081/api/v1/email-verification";

    public Long signup(SignupRequestDto requestDto) {
        // 활성화된 계정 중에서만 이메일 중복 검사
        UserEntity existingUser = userJpaRepository.findByEmail(requestDto.getEmail())
                .filter(user -> user.getActivated())
                .orElse(null);
                
        if (existingUser != null) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비활성화된 계정이 있는 경우 재사용
        UserEntity inactiveUser = userJpaRepository.findByEmail(requestDto.getEmail())
                .filter(user -> !user.getActivated())
                .orElse(null);

        LocalDate birth = LocalDate.parse(requestDto.getBirth(), DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        int age = Period.between(birth, LocalDate.now()).getYears();
        boolean isAdult = age >= 19;

        UserEntity user;
        if (inactiveUser != null) {
            // 기존 비활성화 계정 재사용
            inactiveUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            inactiveUser.setName(requestDto.getName());
            inactiveUser.setPhoneNumber(requestDto.getPhoneNumber());
            inactiveUser.setBirth(birth);
            inactiveUser.setAge(age);
            inactiveUser.setIsAdult(isAdult);
            inactiveUser.setActivated(true);
            user = inactiveUser;
        } else {
            // 새 계정 생성
            user = UserEntity.builder()
                    .email(requestDto.getEmail())
                    .password(passwordEncoder.encode(requestDto.getPassword()))
                    .name(requestDto.getName())
                    .phoneNumber(requestDto.getPhoneNumber())
                    .birth(birth)
                    .age(age)
                    .isAdult(isAdult)
                    .role(Role.USER)
                    .build();
        }
        return userJpaRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public String findIdByName(FindIdRequestDto findIdRequestDto) {
        return userJpaRepository.findByNameAndPhoneNumber(findIdRequestDto.getName(), findIdRequestDto.getPhoneNumber())
            .map(UserEntity::getEmail)
            .orElseThrow(() -> new IllegalArgumentException("해당 정보로 등록된 사용자가 없습니다."));
    }

    public void sendPasswordResetCode(String email) {
        UserEntity user = userJpaRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        sendEmailVerificationCode(email);
    }

    /**
     * 회원가입용 이메일 인증 코드 발송 (기존 사용자 검증 없음)
     */
    public void sendSignupVerificationCode(String email) {
        sendEmailVerificationCode(email);
    }

    /**
     * 공통 이메일 인증 코드 발송 로직
     */
    private void sendEmailVerificationCode(String email) {
        // 이메일 인증 코드 발송 (최대 3회 재시도)
        int maxRetries = 3;
        int retryCount = 0;
        boolean success = false;
        Exception lastException = null;

        while (retryCount < maxRetries && !success) {
            try {
                if (retryCount > 0) {
                    log.info("[이메일인증] 재시도 {}회차: email={}", retryCount, email);
                    TimeUnit.SECONDS.sleep(1); // 재시도 간 1초 대기
                }

                // Kotlin 서버로 인증 코드 전송 요청
                String url = emailAuthApiBaseUrl + "/send";
                Map<String, String> body = new HashMap<>();
                body.put("email", email);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
                
                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    success = true;
                    log.info("[이메일인증] 발송 성공: email={}, 시도횟수={}", email, retryCount + 1);
                } else {
                    log.warn("[이메일인증] HTTP 오류: email={}, status={}, 시도횟수={}", 
                        email, response.getStatusCode(), retryCount + 1);
                }
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                log.warn("[이메일인증] 발송 실패: email={}, 시도횟수={}, 오류={}", 
                    email, retryCount, e.getMessage());
            }
        }

        if (!success) {
            log.error("[이메일인증] 최종 발송 실패: email={}, 총시도횟수={}, 마지막오류={}", 
                email, retryCount, lastException != null ? lastException.getMessage() : "알 수 없는 오류");
            throw new IllegalArgumentException("이메일 인증 코드 전송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        // 비밀번호 확인
        if (!resetPasswordRequestDto.getNewPassword().equals(resetPasswordRequestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        // 인증 코드 확인 (Kotlin 서버 REST API 호출)
        if (!verifyEmailCodeWithKotlinApi(resetPasswordRequestDto.getEmail(), resetPasswordRequestDto.getVerificationCode())) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않거나 만료되었습니다.");
        }

        // 비밀번호 변경
        UserEntity user = userJpaRepository.findByEmail(resetPasswordRequestDto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userJpaRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public void updateUser(Long userId, UserUpdateRequestDto requestDto) {
        log.info("[회원정보수정] 호출: userId={}, name={}, phoneNumber={}, newPassword={}, currentPassword={}",
            userId, requestDto.getName(), requestDto.getPhoneNumber(), requestDto.getNewPassword(), requestDto.getCurrentPassword());
        UserEntity user = findById(userId);

        // 비밀번호 변경이 요청된 경우
        if (requestDto.getNewPassword() != null && !requestDto.getNewPassword().isEmpty()) {
            // 현재 비밀번호 확인
            if (requestDto.getCurrentPassword() == null || requestDto.getCurrentPassword().isEmpty()) {
                log.warn("[회원정보수정] 현재 비밀번호 미입력");
                throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
            }
            if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
                log.warn("[회원정보수정] 현재 비밀번호 불일치");
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            // 새 비밀번호 확인
            if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
                log.warn("[회원정보수정] 새 비밀번호 불일치");
                throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
            }
            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            log.info("[회원정보수정] 비밀번호 변경 완료");
        }

        // 이름과 전화번호 업데이트
        user.setName(requestDto.getName());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        log.info("[회원정보수정] 이름/전화번호 변경: name={}, phoneNumber={}", requestDto.getName(), requestDto.getPhoneNumber());

        userJpaRepository.save(user);
        log.info("[회원정보수정] 저장 완료: userId={}", userId);
    }

    public void deleteUser(Long userId) {
        log.info("[회원탈퇴] 호출: userId={}", userId);
        UserEntity user = findById(userId);
        user.unActivated();
        userJpaRepository.save(user);
        log.info("[회원탈퇴] 비활성화 및 저장 완료: userId={}", userId);
    }

    @Transactional(readOnly = true, transactionManager = "jpaTransactionManager")
    public UserInfoDto findUserInfoById(Long id) {
        UserEntity user = userJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserInfoDto.from(user);
    }

    @Transactional(readOnly = true, transactionManager = "jpaTransactionManager")
    public List<RatedMovieDto> findRatedMoviesByUserId(Long userId) {
        UserEntity user = userJpaRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return ratingJpaRepository.findByUser(user).stream()
            .map(RatedMovieDto::from)
            .toList();
    }

    @Transactional(readOnly = true, transactionManager = "jpaTransactionManager")
    public List<WishlistMovieDto> findWishlistMoviesByUserId(Long userId) {
        UserEntity user = userJpaRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return interestJpaRepository.findByUserAndStatus(user, Status.WATCH_LATER).stream()
            .map(WishlistMovieDto::from)
            .toList();
    }

    protected boolean verifyEmailWithKotlinApi(String email) {
        String url = emailAuthApiBaseUrl + "/verify";
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("code", ""); // 빈 코드로 인증 상태만 확인
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return Boolean.TRUE.equals(response.getBody().get("verified"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyEmailCodeWithKotlinApi(String email, String code) {
        String url = emailAuthApiBaseUrl + "/verify";
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("code", code);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            return Boolean.TRUE.equals(response.getBody().get("verified"));
        } catch (Exception e) {
            return false;
        }
    }
} 