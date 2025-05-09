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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Value("${remember-me.key}")
  private String rememberMeKey;

  private final CustomUserDetailsService userDetailsService;

  @Bean
  public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder)
      throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
    return builder.build();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())

        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .successHandler(new CustomLoginSuccessHandler())
            .failureHandler(new CustomLoginFailureHandler())
            .permitAll()
        )

        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID", "remember-me")
        )

        .rememberMe(rm -> rm
            .key(rememberMeKey)
            .tokenValiditySeconds(86400 * 30)
            .userDetailsService(userDetailsService)
            .rememberMeParameter("remember-me")
        )

        .exceptionHandling(ex -> ex
            .accessDeniedHandler(new CustomAccessDeniedHandler())
        )

        .authorizeHttpRequests(auth -> auth
//            .requestMatchers("/admin/**").hasRole("ADMIN")
//            .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/error").permitAll()
            .anyRequest().permitAll()
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
