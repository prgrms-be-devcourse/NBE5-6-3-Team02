package com.grepp.smartwatcha.infra.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

// Spring Security 로그인 실패 시 동작하는 핸들러
// 기능:
// - 로그인 실패 원인을 로그로 기록
// - 로그인 페이지로 리다이렉트하며 쿼리 파라미터로 오류 표시

@Slf4j
@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

  // 인증 실패 시 실행되는 메서드
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {

    // 로그인 실패 사유 로깅
    log.info("🔒 [로그인 실패] {}", exception.getMessage());

    // 로그인 페이지로 리다이렉트 (에러 플래그 포함)
    response.sendRedirect("/login?error=true");
  }
}
