package com.grepp.smartwatcha.app.model.details.dto.jpadto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailsDto {
    private Long id;
    private String title;
    private LocalDateTime releaseDate;
    private String country;
    private String overview;
    private String poster;
    private String certification;
    private Boolean isReleased;
}

