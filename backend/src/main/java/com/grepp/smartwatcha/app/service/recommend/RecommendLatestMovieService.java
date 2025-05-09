package com.grepp.smartwatcha.app.service.recommend;

import com.grepp.smartwatcha.infra.jpa.recommend.MovieRecommendLatestJpaRepository;
import com.grepp.smartwatcha.infra.response.recommend.MovieRecommendLatestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendLatestMovieService {

    private final MovieRecommendLatestJpaRepository movieRecommendLatestJpaRepository;

    public List<MovieRecommendLatestResponse> getLatestMovieList() {
        return movieRecommendLatestJpaRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(MovieRecommendLatestResponse::from)
                .collect(Collectors.toList());
    }
}
