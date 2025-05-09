package com.grepp.smartwatcha.app.details.service;

import com.grepp.smartwatcha.app.details.dto.MovieDetailsDTO;
import com.grepp.smartwatcha.app.details.repository.MovieDetailsRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "transactionManager")
public class MovieService {

    private final MovieDetailsRepository movieRepository;

    public MovieDetailsDTO getMovieDetail(Long movieId) {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));

        return new MovieDetailsDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getCountry(),
                movie.getPoster(),
                movie.getCertification(),
                movie.getIsReleased()
        );
    }

    public Double getAverageScore(Long movieId) {
        Double avg = movieRepository.findAverageScore(movieId);
        return avg != null ? avg : 0.0;
    }
}
