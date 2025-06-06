package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpcomingMovieCastDto { // 영화 배우 정보 DTO
    private String name;
    private int order;
}
