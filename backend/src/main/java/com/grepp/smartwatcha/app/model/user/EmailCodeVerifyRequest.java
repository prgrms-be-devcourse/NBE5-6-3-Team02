package com.grepp.smartwatcha.app.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCodeVerifyRequest {
    private String email;
    private String code;
} 