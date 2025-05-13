package com.grepp.smartwatcha.app.model.user.dto;

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
public class UserUpdateRequestDto {
    private String name;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
} 