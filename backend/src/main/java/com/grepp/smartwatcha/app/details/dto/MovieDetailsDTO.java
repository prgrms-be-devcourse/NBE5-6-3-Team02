package com.grepp.smartwatcha.app.details.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailsDTO {
    private Long id;
    private String title;
    private String country;
    private String posterUrl;
    private String certification;
    private Boolean isReleased;
}

