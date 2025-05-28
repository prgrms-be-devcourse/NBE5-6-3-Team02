package com.grepp.smartwatcha.infra.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

// Spring Security 로그인 성공 시 실행되는 핸들러
// 기능:
  // - 로그인한 사용자의 권한(Role)을 확인
  // - ROLE_ADMIN 이면 관리자 페이지로 리다이렉트

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

  // 로그인 성공 후 실행되는 메서드
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    // 로그인한 사용자의 권한 목록
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    // ROLE_ADMIN 권한이 있는 경우 관리자 페이지로 리다이렉트
    for(GrantedAuthority authority : authorities) {
      if(authority.getAuthority().equals("ROLE_ADMIN")) {
        response.sendRedirect("/admin");
        return;
      }
    }

    // 일반 유저는 홈 페이지로 이동
    response.sendRedirect("/");
  }
}
