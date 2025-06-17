package com.grepp.smartwatcha.app.controller.api.recommend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MovieCreatedAtResponse {
    private Long userId;
    private Long movieId;
    private double score;
    private LocalDateTime createdAt;
}