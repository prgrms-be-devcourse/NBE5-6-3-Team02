package com.grepp.smartwatcha.app.model.search.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchJpaRepository extends JpaRepository<MovieEntity, Long> {

    @Override
    Optional<MovieEntity> findById(Long id);

    List<MovieEntity> findByTitle(String title);

    List<MovieEntity> findByYear(int year);

    List<MovieEntity> findByCountry(String country);
}
