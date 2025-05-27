package com.grepp.smartwatcha.app.controller.api.search.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
// FastApi response를 담을 객체
public class SmartSearchApiResponse {
    @JsonProperty("movie_ids")
    List<MovieWrapper> movieIds;

    @Data
    // 실제 영화 id를 담는 Wrapper
    public static class MovieWrapper {
        private Long movie;
    }

}