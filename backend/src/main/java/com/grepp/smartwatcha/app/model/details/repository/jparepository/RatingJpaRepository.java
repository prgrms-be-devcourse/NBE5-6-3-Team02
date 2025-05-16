package com.grepp.smartwatcha.app.model.details.repository.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingJpaRepository extends JpaRepository<RatingEntity, Long> {

    Optional<RatingEntity> findByUserAndMovie(UserEntity user, MovieEntity movie);


    @Query("SELECT FLOOR(r.score), COUNT(r) FROM RatingEntity r WHERE r.movie.id = :movieId GROUP BY FLOOR(r.score)")
    List<Object[]> countRatingsByScore(@Param("movieId") Long movieId);

    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.id = :movieId")
    Double getAverageRating(@Param("movieId") Long movieId);

}
