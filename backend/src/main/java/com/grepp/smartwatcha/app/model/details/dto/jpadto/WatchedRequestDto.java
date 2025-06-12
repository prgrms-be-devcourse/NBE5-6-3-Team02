package com.grepp.smartwatcha.app.model.details.dto.jpadto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchedRequestDto {
    private Long movieId;
    private Long userId;

    // 유저가 선택한 날짜를 저장
    private LocalDateTime watchedDate;
}
