package com.grepp.smartwatcha.app.model.recommend.service.highestrated;

import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieRecommendHighestRatedJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(value = "jpaTransactionManager", readOnly = true)
public class RecommendHighestRatedJpaService {

    private final MovieRecommendHighestRatedJpaRepository ratingRepo;
    private final MovieQueryJpaRepository movieRepo;

    // 평균 평점이 가장 높은 상위 10개 영화 조회
    public List<Object[]> getTop10MovieIdAndScores() {
        Pageable top10 = PageRequest.of(0, 10);
        return ratingRepo.findTop10ByAverageRating(top10);
    }

    // 주어진 영화id 로 movieEntity 조회
    public MovieEntity findMovieById(Long id) {
        return movieRepo.findById(id).orElse(null);
    }
}