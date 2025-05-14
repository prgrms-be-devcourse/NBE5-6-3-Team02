package com.grepp.smartwatcha.app.model.details.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingJpaRepository extends JpaRepository<RatingEntity, Long> {
    @Query("SELECT FLOOR(r.score), COUNT(r) FROM RatingEntity r WHERE r.movie.id = :movieId GROUP BY FLOOR(r.score)")
    List<Object[]> countRatingsByScore(@Param("movieId") Long movieId);

}
