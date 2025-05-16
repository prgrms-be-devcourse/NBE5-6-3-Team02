package com.grepp.smartwatcha.app.model.admin.upcoming.repository.jpa;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpcomingMovieJpaRepository extends JpaRepository<MovieEntity, Long> {
  boolean existsById(Long id);
  Page<MovieEntity> findByIsReleasedFalseAndReleaseDateAfter(LocalDateTime now, Pageable pageable);
}
