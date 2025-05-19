package com.grepp.smartwatcha.app.model.admin.movie.list;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminMovieJpaRepository extends JpaRepository<MovieEntity, Long>,
    JpaSpecificationExecutor<MovieEntity> {
  // 대시보드용 통계 메서드
  long countByReleaseDateAfter(LocalDateTime now);  // 공개 예정 영화 수
  List<MovieEntity> findTop5ByReleaseDateAfterOrderByReleaseDateAsc(LocalDateTime now);
}
