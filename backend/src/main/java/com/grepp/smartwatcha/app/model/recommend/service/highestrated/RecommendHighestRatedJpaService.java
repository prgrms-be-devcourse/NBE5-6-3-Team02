package com.grepp.smartwatcha.app.model.recommend.service.highestrated;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.RecommendHighestRatedMovieJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendHighestRatedJpaService {

    private final RecommendHighestRatedMovieJpaRepository ratingRepo;
    private final MovieQueryJpaRepository movieRepo;

    // 평균 평점이 가장 높은 상위 10개 영화 조회
    @Transactional("jpaTransactionManager")
    public List<Object[]> getTop10MovieIdAndScores() {
        Pageable top10 = PageRequest.of(0, 10);
        return ratingRepo.findTop10ByAverageRating(top10);
    }

    // 주어진 영화id 로 movieEntity 조회
    @Transactional("jpaTransactionManager")
    public MovieEntity findMovieById(Long id) {
        return movieRepo.findById(id).orElse(null);
    }
}