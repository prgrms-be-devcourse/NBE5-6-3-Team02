package com.grepp.smartwatcha.app.controller.api.admin.upcoming.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/*
 * TMDB 영화 개봉일 및 관람등급 API 응답 클래스
 * TMDB API 에서 반환하는 영화의 개봉일과 관람등급 정보를 담는 응답 객체
 * 국가별 개봉일과 관람등급 정보를 포함
 */
@Data
public class UpcomingMovieReleaseDateApiResponse {
    // 국가별 개봉 정보 목록
    private List<CountryReleaseDates> results;

    @Data
    public static class CountryReleaseDates { //국가별 개봉 정보를 담는 내부 클래스
        private String iso_3166_1;
        private List<ReleaseDateDetail> release_dates;
    }

    @Data
    public static class ReleaseDateDetail { //  개봉 정보 상세를 담는 내부 클래스(관람등급, 개봉 유형, 개봉일)

        @JsonProperty("certification")
        private String certification;

        @JsonProperty("type")
        private Integer type;

        @JsonProperty("release_date")
        private String releaseDate;
    }
}
