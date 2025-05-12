package com.grepp.smartwatcha.app.controller.api.search.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class SmartSearchApiResponse {
    @JsonProperty("movie_ids")
    List<MovieWrapper> movieIds;

    @Data
    public static class MovieWrapper {
        private Long movie;
    }

}
