package com.grepp.smartwatcha.app.model.details.dto.jpadto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarMovieDto {
    private Long id;
    private String title;
    private String poster;
}
