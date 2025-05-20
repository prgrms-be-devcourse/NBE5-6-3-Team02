package com.grepp.smartwatcha.app.model.details.service.jpaservice;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.SimilarMovieDto;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieDetailsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class MovieRecommendService {
    private final MovieDetailsJpaRepository movieDetailsJpaRepository;

    public List<SimilarMovieDto> getMoviesByIds(List<Long> ids) {
        return movieDetailsJpaRepository.findByIdIn(ids).stream()
                .map(m -> new SimilarMovieDto(m.getId(),m.getTitle(),m.getPoster()))
                .collect(Collectors.toList());
    }
}
