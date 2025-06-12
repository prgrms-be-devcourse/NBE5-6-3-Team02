package com.grepp.smartwatcha.app.model.search.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchJpaRepository extends JpaRepository<MovieEntity, Long> {

    List<MovieEntity> findByTitle(String title);

    @Query("""
    SELECT m FROM MovieEntity m
    WHERE m.releaseDate BETWEEN :startDate AND :endDate
""")
    List<MovieEntity> findByYear(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<MovieEntity> findByCountry(String country);

}
