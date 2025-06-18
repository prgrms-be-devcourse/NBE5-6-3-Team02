package com.grepp.smartwatcha.app.controller.api.recommend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class MovieGenreDto {

    private Long movieId;
    private List<String> genres;
}