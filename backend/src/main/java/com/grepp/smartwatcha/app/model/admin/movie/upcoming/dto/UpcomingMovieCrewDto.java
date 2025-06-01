package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpcomingMovieCrewDto { // 영화 제작진 정보 DTO
    private String name;
    private String job;
}
