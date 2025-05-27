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
// FastApi 통신을 위한 OpenFeign Api
public interface SmartSearchApi {

    @PostMapping(consumes = "application/json")
    /*
     * FastApi 통신 함수
     * 입력: xInternalToken, request
     * 출력: FastApi 응답 코드
     * 로직: xInternalToken을 통해 내부에서 전달된 함수임을 인증, request에 담긴 정보를 통해 llm 요청
     */
    SmartSearchApiResponse call(
            @RequestHeader("X-Internal-Token") String xInternalToken,
            @RequestBody SmartSearchApiRequest request
            );
}