package com.grepp.smartwatcha.app.model.recommend.service.latest;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieRecommendLatestJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendLatestRatedJpaService {

    private final MovieRecommendLatestJpaRepository movieRepo;
    private final MovieQueryJpaRepository ratingRepo;

    // 최신 영화 10개 조회
    @Transactional("jpaTransactionManager")
    public List<MovieEntity> getTop10LatestMovies() {
        return movieRepo.findTop10ByOrderByCreatedAtDesc();
    }

    // 영화의 평균 별점 조회
    public Double getAverageScore(Long movieId) {
        Double avg = ratingRepo.findAverageScoreByMovieId(movieId);
        return avg != null ? avg : 0.0;
    }
}