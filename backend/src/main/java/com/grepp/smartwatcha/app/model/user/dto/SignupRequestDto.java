package com.grepp.smartwatcha.app.model.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.grepp.smartwatcha.app.model.user.validation.ValidationPatterns;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.format}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Pattern(regexp = ValidationPatterns.PASSWORD_PATTERN, message = "{validation.password.format}")
    private String password;

    @NotBlank(message = "{validation.password.required}")
    private String confirmPassword;

    @NotBlank(message = "{validation.name.required}")
    private String name;

    @NotBlank(message = "{validation.phone.required}")
    @Pattern(regexp = ValidationPatterns.PHONE_NUMBER_PATTERN, message = "{validation.phone.format}")
    private String phoneNumber;

    @NotBlank(message = "생년월일은 필수 입력값입니다")
    @Pattern(regexp = "^\\d{4}\\.\\d{2}\\.\\d{2}$", message = "생년월일 형식이 올바르지 않습니다 (예: 2000.04.20)")
    private String birth; // "2000.04.20" 형식
} 