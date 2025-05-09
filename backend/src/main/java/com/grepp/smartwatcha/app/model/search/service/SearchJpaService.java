package com.grepp.smartwatcha.app.model.search.service;

import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.app.model.search.repository.SearchJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class SearchJpaService {

    private final SearchJpaRepository searchJpaRepository;

    public List<SearchResultDto> findByIds(List<Long> ids) {
        List<SearchResultDto> searchResultDtos = new ArrayList<>();
        for (Long id : ids) {
            Optional<MovieEntity> movieEntity = searchJpaRepository.findById(id);

            if (movieEntity.isPresent()) {
                SearchResultDto searchResultDto = SearchResultDto.fromEntity(movieEntity.get());
                searchResultDtos.add(searchResultDto);
            }
        }
        return searchResultDtos;
    }

    public List<SearchResultDto> findByTitle(String title) {
        List<MovieEntity> movieEntities = searchJpaRepository.findByTitle(title);
        List<SearchResultDto> searchResultDtos = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntities) {
            SearchResultDto searchResultDto = SearchResultDto.fromEntity(movieEntity);
            searchResultDtos.add(searchResultDto);
        }

        return searchResultDtos;
    }

    public List<SearchResultDto> findByYear(int year) {
        List<MovieEntity> movieEntities = searchJpaRepository.findByYear(year);
        List<SearchResultDto> searchResultDtos = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntities) {
            SearchResultDto searchResultDto = SearchResultDto.fromEntity(movieEntity);
            searchResultDtos.add(searchResultDto);
        }

        return searchResultDtos;
    }

    public List<SearchResultDto> findByCountry(String country) {
        List<MovieEntity> movieEntities = searchJpaRepository.findByCountry(country);
        List<SearchResultDto> searchResultDtos = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntities) {
            SearchResultDto searchResultDto = SearchResultDto.fromEntity(movieEntity);
            searchResultDtos.add(searchResultDto);
        }

        return searchResultDtos;
    }
}
