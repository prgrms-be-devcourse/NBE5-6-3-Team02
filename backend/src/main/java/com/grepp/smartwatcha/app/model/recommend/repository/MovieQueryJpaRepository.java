package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieQueryJpaRepository extends JpaRepository<MovieEntity, Long> {

    // 공개된 영화 목록을 조회함
    @Query("SELECT m FROM MovieEntity m WHERE m.isReleased = true")
    List<MovieEntity> findAllReleased();

    // 여러개의 id를 조회
    @Query("SELECT m FROM MovieEntity m WHERE m.id IN :ids")
    List<MovieEntity> findByIdIn(@Param("ids") List<Long> ids);

    // 영화 id 기준으로 별점에 대한 평균 계산
    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.id = :movieId")
    Double findAverageScoreByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT m FROM MovieEntity m WHERE m.id NOT IN " +
            "(SELECT r.movie.id FROM RatingEntity r WHERE r.user.id = :userId)")
    List<MovieEntity> findMoviesNotRatedByUser(@Param("userId") Long userId);

    @Query("SELECT r.movie.id, AVG(r.score) FROM RatingEntity r " +
            "WHERE r.movie.id IN :movieIds GROUP BY r.movie.id")
    List<Object[]> findAverageScoresByMovieIds(@Param("movieIds") List<Long> movieIds);
}