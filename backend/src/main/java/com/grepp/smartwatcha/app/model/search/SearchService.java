package com.grepp.smartwatcha.app.model.search;

import com.grepp.smartwatcha.app.model.search.code.QueryCache;
import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.app.model.search.repository.SearchJpaRepository;
import com.grepp.smartwatcha.app.model.search.service.SearchJpaService;
import com.grepp.smartwatcha.app.model.search.service.SearchNeo4jService;
import groovy.lang.GString;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final SearchJpaService searchJpaService;
    private final SearchNeo4jService searchNeo4jService;
    private final QueryCache queryCache;

    public String buildQueryKey(String type, String intent, String query) {
        return type + ":" + intent + ":" + query;
    }

    public String hash(String queryKey) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(queryKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<SearchResultDto> searchAndCache(String hash, List<Long> ids) {

        if (!queryCache.exists(hash)) {
            List<SearchResultDto> searchResultDtos = searchJpaService.findByIds(ids);
            queryCache.store(hash, searchResultDtos);
        }

        return queryCache.get(hash);
    }

    public boolean isHash(String hash) {
        if (!queryCache.exists(hash)) {
            return false;
        }
        return true;
    }


    public Page<SearchResultDto> getPaged(String queryHash, Pageable pageable) {
        List<SearchResultDto> all = queryCache.get(queryHash);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());

        List<SearchResultDto> subList = all.subList(start, end);
        return new PageImpl<>(subList, pageable, all.size());
    }

    public List<Long> findByTitle(String title) {
        return searchJpaService.findByTitle(title);
    }

    public List<Long> findByYear(int year) {
        return searchJpaService.findByYear(year);
    }

    public List<Long> findByCountry(String country) {
        return searchJpaService.findByCountry(country);
    }
//
//    public List<SearchResultDto> findByIds(List<Long> movieIdList) {
//        return searchJpaService.findByIds(movieIdList);
//    }

    public List<Long> findByGenre(String genre) {
        return searchNeo4jService.findByGenre(genre);
    }

    public List<Long> findByActor(String actor) {
        return searchNeo4jService.findByActor(actor);
    }

    public List<Long> findByDirector(String director) {
        return searchNeo4jService.findByDirector(director);
    }

    public List<Long> findByWriter(String writer) {
        return searchNeo4jService.findByWriter(writer);
    }
}
