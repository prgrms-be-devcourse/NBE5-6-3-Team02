package com.grepp.smartwatcha.app.model.recommend.repository;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RecommendHighestRatedMovieJpaRepository extends JpaRepository<RatingEntity, Long> {

    //각 영화별로 평균 점수 계산 후 높은 순으로 정렬
    @Query("SELECT r.movie.id, AVG(r.score) as avgScore " +
            "FROM RatingEntity r " +
            "GROUP BY r.movie.id " +
            "ORDER BY avgScore DESC")
    List<Object[]> findTop10ByAverageRating(Pageable pageable);
}

