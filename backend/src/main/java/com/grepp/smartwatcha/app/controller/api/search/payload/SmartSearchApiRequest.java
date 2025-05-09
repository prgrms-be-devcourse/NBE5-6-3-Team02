package com.grepp.smartwatcha.app.controller.api.search.payload;

import lombok.Data;

@Data
public class SmartSearchApiRequest {
    private String intent;
    private String query;
}
