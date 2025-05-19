package com.grepp.smartwatcha.app.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String email;
    private String name;
    private String phoneNumber;
    // 필요시 생성자, builder 등 추가
    public static UserInfoDto from(com.grepp.smartwatcha.infra.jpa.entity.UserEntity entity) {
        return new UserInfoDto(
            entity.getEmail(),
            entity.getName(),
            entity.getPhoneNumber()
        );
    }
} 