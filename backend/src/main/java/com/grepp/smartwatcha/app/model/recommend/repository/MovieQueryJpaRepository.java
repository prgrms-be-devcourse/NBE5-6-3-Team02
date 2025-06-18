package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieQueryJpaRepository extends JpaRepository<MovieEntity, Long> {

    // 출시된 모든 영화 조회
    @Query("SELECT m FROM MovieEntity m WHERE m.isReleased = true")
    List<MovieEntity> findAllReleased();

    // id에 해당하는 영화 조회
    @Query("SELECT m FROM MovieEntity m WHERE m.id IN :ids")
    List<MovieEntity> findByIdIn(@Param("ids") List<Long> ids);

    // 영화의 평균 별점 계산
    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.id = :movieId")
    Double findAverageScoreByMovieId(@Param("movieId") Long movieId);

    // 사용자가 아직 평가하지 않은 영화 조회
    @Query("SELECT m FROM MovieEntity m WHERE m.id NOT IN " +
            "(SELECT r.movie.id FROM RatingEntity r WHERE r.user.id = :userId)")
    List<MovieEntity> findMoviesNotRatedByUser(@Param("userId") Long userId);

    // 영화 id 목록에 각 영화의 평균 별점 반환
    @Query("SELECT r.movie.id, AVG(r.score) FROM RatingEntity r " +
            "WHERE r.movie.id IN :movieIds GROUP BY r.movie.id")
    List<Object[]> findAverageScoresByMovieIds(@Param("movieIds") List<Long> movieIds);
}