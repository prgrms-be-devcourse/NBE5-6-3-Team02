package com.grepp.smartwatcha.app.admin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
  public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String raw = "1234"; // 인코딩할 비밀번호
    String encoded = encoder.encode(raw);
    System.out.println(encoded);
  }
}
