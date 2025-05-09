package com.grepp.smartwatcha.app.controller.api.search;

import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchApiRequest;
import com.grepp.smartwatcha.app.controller.api.search.payload.SmartSearchResponse;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmartSearchApiTest {

    @Autowired
    SmartSearchApi smartSearchApi;

    @Test
    void test() {
        SmartSearchApiRequest request = new SmartSearchApiRequest();
        request.setIntent("recommend");
        request.setQuery("recommend some emotional movie");
        SmartSearchResponse response = smartSearchApi.call("123qwe!@#", request);
        System.out.println(response);
        List<Long> movieIdList = response.getMovieIds().stream()
                .map(SmartSearchResponse.MovieWrapper::getMovie)
                .toList();

        System.out.println(movieIdList);
    }
}