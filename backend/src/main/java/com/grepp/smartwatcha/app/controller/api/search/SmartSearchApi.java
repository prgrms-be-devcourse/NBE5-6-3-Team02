package com.grepp.smartwatcha.app.controller.api.search;

import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchApiRequest;
import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchApiResponse;
import com.grepp.smartwatcha.infra.config.FeignCommonConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "smart-search-api",
        url = "http://localhost:8000/llm/search",
        configuration = {FeignCommonConfig.class})
public interface SmartSearchApi {

    @PostMapping(consumes = "application/json")
    SmartSearchApiResponse call(
            @RequestHeader("X-Internal-Token") String xInternalToken,
            @RequestBody SmartSearchApiRequest request
            );
}
