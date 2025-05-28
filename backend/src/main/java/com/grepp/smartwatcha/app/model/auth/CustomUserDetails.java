package com.grepp.smartwatcha.app.model.auth;

import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Spring Security 인증 시 사용되는 사용자 정보 클래스
// UserEntity 를 기반으로 UserDetails 인터페이스를 구현하여,
// Spring Security 가 인증 후 세션에서 사용자 정보를 추적할 수 있도록 제공

public class CustomUserDetails implements UserDetails {

  private final UserEntity user;

  // 생성자: 인증 대상이 되는 사용자 정보를 주입받음
  public CustomUserDetails(UserEntity user) {
    this.user = user;
  }

  public UserEntity getUser() { // UserEntity getter
    return user;
  }

  // 사용자의 권한(Role)을 반환 - ROLE_ 접두사를 붙여 Spring Security 규칙에 맞게 반환
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
    );
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  } // 사용자 비밀번호 반환

  @Override
  public String getUsername() {
    return user.getEmail();
  } // 사용자 이름 (이메일) 반환

  @Override
  public boolean isAccountNonExpired() {
    return true;
  } // 계정 만료 여부 반환

  @Override
  public boolean isAccountNonLocked() {
    return true;
  } // 계정 잠김 여부 반환

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  } // 자격 증명(비밀번호 등) 만료 여부

  @Override
  public boolean isEnabled() {
    return true;
  } // 계정 활성화 여부

  public String getName() {
    return user.getName();
  } // 사용자 이름(실명) 반환

  public Role getRole() {
    return user.getRole();
  } // 사용자 권한(Role) 반환

  public Long getId() {
    return user.getId();
  } // 사용자 ID 반환
}
