package com.grepp.smartwatcha.app.model.auth;

import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security 의 사용자 인증 과정에서 사용되는 커스텀 UserDetailsService 구현체
// - 입력받은 이메일(email)로 DB 에서 유저 정보를 조회
// - 존재하지 않을 경우 CommonException(UNAUTHORIZED) 예외 발생
// - 존재할 경우 CustomUserDetails 객체로 변환하여 반환

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserJpaRepository userRepository;

  // Spring Security 의 로그인 시 호출되는 메서드
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.info("🔒 [인증 실패] 존재하지 않는 이메일로 로그인 시도: {}", email);
          return new CommonException(ResponseCode.UNAUTHORIZED);
        });

    return new CustomUserDetails(user);
  }
}
