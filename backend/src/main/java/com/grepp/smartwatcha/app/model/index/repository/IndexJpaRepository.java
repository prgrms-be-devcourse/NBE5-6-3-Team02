package com.grepp.smartwatcha.app.model.index.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexJpaRepository extends JpaRepository<MovieEntity, Long> {

    @Query("SELECT m FROM MovieEntity m ORDER BY m.releaseDate LIMIT 10")
    List<MovieEntity> findByReleaseDate();

    @Query("SELECT m FROM MovieEntity m ORDER BY rand() LIMIT 10")
    List<MovieEntity> findByRandom();
}
