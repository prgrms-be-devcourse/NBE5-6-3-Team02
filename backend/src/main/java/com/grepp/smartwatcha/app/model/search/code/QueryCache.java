package com.grepp.smartwatcha.app.model.search.code;

import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class QueryCache {
    private final Map<String, List<SearchResultDto>> cache = new ConcurrentHashMap<>();

    public void store(String queryHash, List<SearchResultDto> result) {
        cache.put(queryHash, result);
    }

    public List<SearchResultDto> get(String queryHash) {
        return cache.get(queryHash);
    }

    public boolean exists(String queryHash) {
        return cache.containsKey(queryHash);
    }
}