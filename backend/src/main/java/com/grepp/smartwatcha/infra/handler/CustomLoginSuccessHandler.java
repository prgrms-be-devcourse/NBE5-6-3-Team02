package com.grepp.smartwatcha.infra.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

// Spring Security 로그인 성공 시 실행되는 핸들러
// 기능:
  // - 로그인한 사용자의 권한(Role)을 확인
  // - ROLE_ADMIN 이면 관리자 페이지로 리다이렉트
  // - 일반 사용자는 이전에 접근하려던 페이지로 리다이렉트

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final RequestCache requestCache = new HttpSessionRequestCache();

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

    // 이전에 접근하려던 페이지가 있는지 확인
    SavedRequest savedRequest = requestCache.getRequest(request, response);
    if (savedRequest != null) {
      // 이전 페이지로 리다이렉트
      String targetUrl = savedRequest.getRedirectUrl();
      response.sendRedirect(targetUrl);
    } else {
      // 이전 페이지 정보가 없으면 홈으로 리다이렉트
      response.sendRedirect("/");
    }
  }
}
