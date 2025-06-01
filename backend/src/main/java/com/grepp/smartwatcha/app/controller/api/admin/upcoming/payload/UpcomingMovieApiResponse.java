package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import java.util.List;
import lombok.Data;

/*
 * TMDB 공개 예정작 API 응답 클래스
 * TMDB API 에서 반환하는 공개 예정작 목록을 담는 응답 객체
 * 페이지네이션 정보와 날짜 범위를 포함
 */
@Data
public class UpcomingMovieApiResponse {

    private Dates dates;
    private int page;

    @JsonProperty("results")
    private List<UpcomingMovieDto> movies;

    @JsonProperty("total_results")
    private int totalResults;

    @JsonProperty("total_pages")
    private int totalPages;

    @Data
    public static class Dates {
        private String maximum;
        private String minimum;
    }
}
