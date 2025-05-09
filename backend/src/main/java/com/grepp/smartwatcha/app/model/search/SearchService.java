package com.grepp.smartwatcha.app.model.search;

import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.app.model.search.repository.SearchJpaRepository;
import com.grepp.smartwatcha.app.model.search.service.SearchJpaService;
import com.grepp.smartwatcha.app.model.search.service.SearchNeo4jService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final SearchJpaService searchJpaService;
    private final SearchNeo4jService searchNeo4jService;


    public List<SearchResultDto> findByTitle(String title) {
        return searchJpaService.findByTitle(title);
    }

    public List<SearchResultDto> findByYear(int year) {
        return searchJpaService.findByYear(year);
    }

    public List<SearchResultDto> findByCountry(String country) {
        return searchJpaService.findByCountry(country);
    }

    public List<SearchResultDto> findByIds(List<Long> movieIdList) {
        return searchJpaService.findByIds(movieIdList);
    }

    public List<SearchResultDto> findByGenre(String genre) {
        List<Long> ids = searchNeo4jService.findByGenre(genre);

        return searchJpaService.findByIds(ids);
    }

    public List<SearchResultDto> findByActor(String actor) {
        List<Long> ids = searchNeo4jService.findByActor(actor);

        return searchJpaService.findByIds(ids);
    }

    public List<SearchResultDto> findByDirector(String director) {
        List<Long> ids = searchNeo4jService.findByDirector(director);

        return searchJpaService.findByIds(ids);
    }

    public List<SearchResultDto> findByWriter(String writer) {
        List<Long> ids = searchNeo4jService.findByWriter(writer);
        
        return searchJpaService.findByIds(ids);
    }
}
