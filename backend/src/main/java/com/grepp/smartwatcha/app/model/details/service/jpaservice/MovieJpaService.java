package com.grepp.smartwatcha.app.model.details.service.jpaservice;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.MovieDetailsDto;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieDetailsJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class MovieJpaService {

    private final MovieDetailsJpaRepository movieRepository;

    public MovieDetailsDto getMovieDetail(Long movieId) {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));

        return new MovieDetailsDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getCountry(),
                movie.getOverview(),
                movie.getPoster(),
                movie.getCertification(),
                movie.getIsReleased()
        );
    }

    // movieId 에 해당하는 별점에 정보를 가져와 평균으로 반환
    // null 값일 경우 0.0 으로 반환처리 기능 추가 (null -> 0.0)
    public Double getAverageScore(Long movieId) {
        Double avg = movieRepository.findAverageScore(movieId);
        return avg != null ? avg : 0.0;
    }
}
