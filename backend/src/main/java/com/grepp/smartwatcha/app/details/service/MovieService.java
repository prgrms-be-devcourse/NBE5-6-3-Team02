package com.grepp.smartwatcha.app.details.service;

import com.grepp.smartwatcha.app.details.repository.MovieDetailsRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieDetailsRepository movieRepository;

    public MovieEntity getMovieDetail(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));
    }

    public Double getAverageScore(Long movieId) {
        Double avg = movieRepository.findAverageScore(movieId);
        return avg != null ? avg : 0.0;
    }
}
