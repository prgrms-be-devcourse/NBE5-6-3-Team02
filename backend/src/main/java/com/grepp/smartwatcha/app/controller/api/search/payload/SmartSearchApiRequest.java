package com.grepp.smartwatcha.app.controller.api.search.payload;

import lombok.Data;

@Data
// FastApi request body 구성을 위한 객체
public class SmartSearchApiRequest {
    private String intent;
    private String query;
}