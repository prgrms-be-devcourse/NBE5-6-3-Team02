package com.grepp.smartwatcha.app.model.details.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailsDTO {
    private Long id;
    private String title;
    private int year;
    private String country;
    private String poster;
    private String certification;
    private Boolean isReleased;
}

