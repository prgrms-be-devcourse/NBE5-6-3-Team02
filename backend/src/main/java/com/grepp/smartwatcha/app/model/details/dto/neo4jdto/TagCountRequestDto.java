package com.grepp.smartwatcha.app.model.details.dto.neo4jdto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagCountRequestDto {
    private final String name;
    private final Long count;
}
