package com.grepp.smartwatcha.infra.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.util.function.Supplier

// Spring Security 설정 클래스
@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${internal.token}") private val internalToken: String  // application.yml 등에서 주입받는 내부 인증 토큰
) {

    // Spring Security 필터 체인 설정
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            // CSRF 설정: 특정 경로는 CSRF 검증 제외
            .csrf { csrf ->
                csrf.ignoringRequestMatchers(AntPathRequestMatcher("/admin/movies/upcoming/sync"))
            }

            // 요청별 인가 설정
            .authorizeHttpRequests { auth ->
                auth
                    // 이메일 인증 API 경로는 모든 요청 허용
                    .requestMatchers("/api/v1/email-verification/**").permitAll()
                    
                    // 관리자 동기화 API 경로에 대해 커스텀 인증 로직 적용
                    .requestMatchers("/admin/movies/upcoming/sync")
                    .access { _: Supplier<*>, context: RequestAuthorizationContext ->
                        val request: HttpServletRequest = context.request
                        val token = request.getHeader("x-internal-token")  // 헤더에서 내부 토큰 추출
                        AuthorizationDecision(token == internalToken)      // 토큰이 일치하면 허용
                    }

                    // 그 외 모든 요청은 허용
                    .anyRequest().permitAll()
            }
            .build()
    }
}
