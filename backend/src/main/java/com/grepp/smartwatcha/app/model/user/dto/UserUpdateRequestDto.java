package com.grepp.smartwatcha.app.model.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.grepp.smartwatcha.app.model.user.validation.ValidationPatterns;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
    @NotBlank(message = "{validation.name.required}")
    private String name;

    @NotBlank(message = "{validation.phone.required}")
    @Pattern(regexp = ValidationPatterns.PHONE_NUMBER_PATTERN, message = "{validation.phone.format}")
    private String phoneNumber;

    private String currentPassword;

    @Pattern(regexp = ValidationPatterns.PASSWORD_PATTERN, message = "{validation.password.format}")
    private String newPassword;

    private String confirmPassword;
} 