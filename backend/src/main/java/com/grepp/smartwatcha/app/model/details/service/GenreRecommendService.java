package com.grepp.smartwatcha.app.model.details.service;


import com.grepp.smartwatcha.app.model.details.dto.jpadto.SimilarMovieDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.MovieRecommendService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.MovieNeo4jService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

// Tag Jpa 와 Neo4j의 통합 service
@Service
@RequiredArgsConstructor
public class GenreRecommendService {
    private final MovieNeo4jService movieNeo4jService;
    private final MovieRecommendService movieRecommendService;

    public List<SimilarMovieDto> getGenreSimilarMovies(Long movieId) {
        List<Long> similarIds = movieNeo4jService.findSimilarMovieIds(movieId);

        if (similarIds.isEmpty()) return Collections.emptyList();

        return movieRecommendService.getMoviesByIds(similarIds);
    }
}

