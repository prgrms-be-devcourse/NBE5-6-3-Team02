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
public class FindIdRequestDto {
    private String name;
    private String phoneNumber;
} 