package com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpcomingMovieGenreDto { // 영화 장르 정보 DTO
    private Long id;
    private String name;
}
