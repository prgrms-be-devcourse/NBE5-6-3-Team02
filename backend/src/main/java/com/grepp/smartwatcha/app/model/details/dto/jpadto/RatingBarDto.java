package com.grepp.smartwatcha.app.model.details.dto.jpadto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RatingBarDto {
    private int score;
    private int percent;
}
