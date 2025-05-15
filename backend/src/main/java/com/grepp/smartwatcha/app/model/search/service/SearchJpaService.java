package com.grepp.smartwatcha.app.model.search.service;

import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.app.model.search.repository.SearchJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public List<Long> findByTitle(String title) {
        List<MovieEntity> movieEntities = searchJpaRepository.findByTitle(title);
        List<Long> ids = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntities) {
            Long id = movieEntity.getId();
            ids.add(id);
        }

        return ids;
    }

    public List<Long> findByYear(int year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year + 1, 1, 1, 0, 0, 0);
        List<MovieEntity> movieEntities = searchJpaRepository.findByYear(start, end);
        List<Long> ids = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntities) {
            Long id = movieEntity.getId();
            ids.add(id);
        }

        return ids;
    }

    public List<Long> findByCountry(String country) {
        List<MovieEntity> movieEntities = searchJpaRepository.findByCountry(country);
        List<Long> ids = new ArrayList<>();

        for (MovieEntity movieEntity : movieEntities) {
            Long id = movieEntity.getId();
            ids.add(id);
        }

        return ids;
    }
}
