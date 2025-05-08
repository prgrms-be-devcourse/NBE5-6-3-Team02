package com.grepp.smartwatcha.app.model.search;

import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.app.model.search.repository.SearchJpaRepository;
import com.grepp.smartwatcha.app.model.search.service.SearchJpaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchJpaService searchJpaService;


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
}
