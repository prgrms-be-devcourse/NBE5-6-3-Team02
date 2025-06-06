package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieGenreDto;
import java.util.List;
import lombok.Data;

/*
 * TMDB 영화 장르 API 응답 클래스
 * TMDB API 에서 반환하는 영화 장르 목록을 담는 응답 객체
 */
@Data
public class UpcomingMovieGenreApiResponse {
    // 영화 장르 목록
    private List<UpcomingMovieGenreDto> genres;
}
