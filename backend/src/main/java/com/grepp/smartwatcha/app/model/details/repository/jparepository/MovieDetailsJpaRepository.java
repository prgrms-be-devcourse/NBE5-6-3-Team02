package com.grepp.smartwatcha.app.model.details.repository.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieDetailsJpaRepository extends JpaRepository<MovieEntity, Long> {

    @Query("SELECT AVG(r.score) FROM RatingEntity r WHERE r.movie.id = :movieId")
    Double findAverageScore(@Param("movieId") Long movieId);


}

