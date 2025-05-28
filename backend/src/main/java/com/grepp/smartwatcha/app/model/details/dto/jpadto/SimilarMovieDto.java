package com.grepp.smartwatcha.app.model.details.dto.jpadto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarMovieDto {
    private Long id;
    private String title;
    private String poster;
}
