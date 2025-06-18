package com.grepp.smartwatcha.app.controller.api.recommend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieRatingScoreDto {
    private Long userId;
    private Long movieId;
    private double score;
}