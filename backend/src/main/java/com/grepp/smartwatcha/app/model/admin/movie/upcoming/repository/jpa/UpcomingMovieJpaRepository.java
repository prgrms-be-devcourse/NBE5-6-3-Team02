package com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.jpa;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * 공개 예정작 영화 JPA Repository
 * 공개 예정작 영화의 저장, 조회, 존재 여부 확인 기능 제공
 */
public interface UpcomingMovieJpaRepository extends JpaRepository<MovieEntity, Long> {
    boolean existsById(Long id); // 영화 ID로 존재 여부 확인

    // 미개봉 영화 중 현재 시각 이후 개봉 예정인 영화 목록 조회
    Page<MovieEntity> findByIsReleasedFalseAndReleaseDateAfter(LocalDateTime now, Pageable pageable);
}
