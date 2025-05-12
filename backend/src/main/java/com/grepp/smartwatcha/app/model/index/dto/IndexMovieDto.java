package com.grepp.smartwatcha.app.model.index.dto;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.Data;

@Data
public class IndexMovieDto {
    private Long id;
    private String title;
    private String poster;

    public static IndexMovieDto fromEntity(MovieEntity movieEntity) {
        IndexMovieDto indexMovieDto = new IndexMovieDto();
        indexMovieDto.setId(movieEntity.getId());
        indexMovieDto.setTitle(movieEntity.getTitle());
        indexMovieDto.setPoster(movieEntity.getPoster());
        return indexMovieDto;
    }
}
