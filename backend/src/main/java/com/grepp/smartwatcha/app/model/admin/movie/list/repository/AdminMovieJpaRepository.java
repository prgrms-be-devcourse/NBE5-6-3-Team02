package com.grepp.smartwatcha.app.model.admin.movie.list.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminMovieJpaRepository extends JpaRepository<MovieEntity, Long> {

  // 영화 리스트를 필터링 조건(keyword, isReleased, releaseDate 범위)에 따라 페이징 조회
  // keyword 는 영화 제목의 일부를 검색 (LIKE)
  // isReleased 는 개봉 여부
  // releaseDate 는 fromDate ~ toDate 범위 (LocalDateTime 기준)
  @Query("""
          SELECT m FROM MovieEntity m
          WHERE (:keyword IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:isReleased IS NULL OR m.isReleased = :isReleased)
            AND (:fromDate IS NULL OR m.releaseDate >= :fromDate)
            AND (:toDate IS NULL OR m.releaseDate < :toDate)
      """)
  Page<MovieEntity> searchMoviesByConditions(
      @Param("keyword") String keyword,
      @Param("isReleased") Boolean isReleased,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable
  );

  // 현재 시각 기준으로 개봉일이 지났지만 아직 isReleased = false 인 영화 목록 조회
  @Query("""
          SELECT m FROM MovieEntity m
          WHERE m.releaseDate < CURRENT_TIMESTAMP
            AND m.isReleased = false
      """)
  List<MovieEntity> findPastUnreleasedMovies();

  // 대시보드용 통계 메서드
  long countByReleaseDateAfter(LocalDateTime now);  // 현재 시간 이후 개봉 예정인 영화 수 반환
  List<MovieEntity> findTop5ByReleaseDateAfterOrderByReleaseDateAsc(
      LocalDateTime now); // 현재 시간 이후 개봉 예정 영화 중 가장 빠른 순으로 5개 조회
}
