package com.grepp.smartwatcha.app.model.search.dto;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.Data;

@Data
public class SearchResultDto {

    private Long id;
    private String title;
    private String poster;

    public static SearchResultDto fromEntity(MovieEntity movieEntity) {
        SearchResultDto searchResultDto = new SearchResultDto();
        searchResultDto.setId(movieEntity.getId());
        searchResultDto.setTitle(movieEntity.getTitle());
        searchResultDto.setPoster(movieEntity.getPoster());
        return searchResultDto;
    }
}
