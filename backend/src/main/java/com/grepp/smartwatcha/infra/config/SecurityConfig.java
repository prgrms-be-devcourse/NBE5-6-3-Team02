package com.grepp.smartwatcha.infra.config;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetailsService;
import com.grepp.smartwatcha.infra.handler.CustomAccessDeniedHandler;
import com.grepp.smartwatcha.infra.handler.CustomLoginFailureHandler;
import com.grepp.smartwatcha.infra.handler.CustomLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Spring Security 전체 설정 클래스
// 기능:
    // - 사용자 인증 및 권한 관리
    // - 로그인/로그아웃 커스터마이징
    // - 예외 핸들링
    // - Remember-Me 설정 등

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${remember-me.key}")
    private String rememberMeKey; // remember-me 기능에서 사용할 고유 키

    private final CustomUserDetailsService userDetailsService;

    // 사용자 인증을 위한 AuthenticationManager 빈 설정
    // 사용자 정보 및 비밀번호 암호화 방식을 등록
    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder)
        throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
        return builder.build();
    }

    // Security Filter Chain 구성
    // HTTP 보안 설정 (로그인, 권한, 예외처리 등)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 비활성화 (필요 시 활성화 가능)
            .csrf(csrf -> csrf.disable())

            // 로그인 설정
            .formLogin(form -> form
                .loginPage("/user/login")                // 사용자 정의 로그인 페이지
                .loginProcessingUrl("/user/login")       // 로그인 form action 경로
                .usernameParameter("email")         // 아이디 파라미터 이름
                .passwordParameter("password")      // 비밀번호 파라미터 이름
                .successHandler(new CustomLoginSuccessHandler())  // 로그인 성공 핸들러
                .failureHandler(new CustomLoginFailureHandler())  // 로그인 실패 핸들러
                .permitAll()
            )

            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/user/logout")                   // 로그아웃 요청 URL
                .logoutSuccessUrl("/")                  // 로그아웃 성공 시 이동할 URL
                .invalidateHttpSession(true)            // 세션 무효화
                .deleteCookies("JSESSIONID", "remember-me") // 쿠키 삭제
            )

            // remember-me 설정
            .rememberMe(rm -> rm
                .key(rememberMeKey)                     // 토큰 키 (고유 식별용)
                .tokenValiditySeconds(86400 * 30)       // 토큰 유효기간 (30일)
                .userDetailsService(userDetailsService) // 사용자 정보 제공자
                .rememberMeParameter("remember-me")     // form 의 checkbox name
            )

            // 인가 실패(403) 처리 핸들러 등록
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            )

            // 요청 URL 별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")  // 관리자만 접근 가능
                .requestMatchers(
                    "/",
                    "/user/login",
                    "/user/signup",
                    "/user/signup/send-code",
                    "/user/signup/verify",
                    "/user/find-id",
                    "/user/find-password",
                    "/user/find-password/send-code",
                    "/user/find-password/verify",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error"
                ).permitAll()                // 인증 없이 접근 가능
                .anyRequest().authenticated() // 그 외 요청은 인증 필요
            );

        return http.build();
    }

    // 비밀번호 암호화에 사용되는 PasswordEncoder 빈
    // 다양한 암호화 알고리즘을 지원하는 delegating encoder 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
