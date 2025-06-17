package com.grepp.smartwatcha.app.model.admin.movie.list.repository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Admin 페이지에서 영화별 평균 평점을 조회하기 위한 JPA Repository.
public interface AdminMovieRatingJpaRepository extends JpaRepository<RatingEntity, Long> {

  // 주어진 영화 ID 목록에 대해 영화별 평균 평점을 조회
  // @param movieIds 평균 평점을 조회할 영화 ID 목록
  // @return 각 영화 ID와 해당 영화의 평균 평점을 담은 Object 배열 리스트
  //         (index 0: movieId, index 1: 평균 평점(Double))
  @Query("SELECT r.movie.id, AVG(r.score) FROM RatingEntity r WHERE r.movie.id IN :movieIds GROUP BY r.movie.id")
  List<Object[]> findAverageRatingByMovieIds(@Param("movieIds") List<Long> movieIds);
}
