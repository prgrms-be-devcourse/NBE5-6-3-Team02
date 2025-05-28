package com.grepp.smartwatcha.infra.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

// Spring Security 에서 인가(Authorization) 실패 시 처리하는 핸들러
// -> 인증은 되었지만, 요청한 리소스에 대한 권한이 없는 경우 (403 Forbidden)
// 처리 방식 : "/?error=access_denied" 경로로 리다이렉트

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  // 인가 실패 시 호출되는 메서드
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.sendRedirect("/?error=access_denied");
  }
}
