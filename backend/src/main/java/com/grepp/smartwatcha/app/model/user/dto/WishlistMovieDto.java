package com.grepp.smartwatcha.app.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistMovieDto {
    private Long movieId;
    private String title;
    private String poster;

    public static WishlistMovieDto from(com.grepp.smartwatcha.infra.jpa.entity.InterestEntity entity) {
        return new WishlistMovieDto(
            entity.getMovie().getId(),
            entity.getMovie().getTitle(),
            entity.getMovie().getPoster()
        );
    }
} 