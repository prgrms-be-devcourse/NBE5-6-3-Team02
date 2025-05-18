package com.grepp.smartwatcha.app.model.details.repository.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.querydsl.core.group.GroupBy;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingJpaRepository extends JpaRepository<RatingEntity, Long> {

    Optional<RatingEntity> findByUserAndMovie(UserEntity user, MovieEntity movie);


    @Query("SELECT r.score, COUNT(r) FROM RatingEntity r WHERE r.movie.id = :movieId GROUP BY r.score")
    List<Object[]> countRatingsByScore(@Param("movieId") Long movieId);


    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.id = :movieId")
    Double getAverageRating(@Param("movieId") Long movieId);

    List<RatingEntity> findByUser(UserEntity user);

    @Query("SELECT r FROM RatingEntity r WHERE r.user.id = :userId AND r.movie.id = :movieId")
    Optional<RatingEntity> findRatingByUserAndMovie(@Param("userId") Long userId, @Param("movieId") Long movieId);
}
