package com.grepp.smartwatcha.app.model.admin.user.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserTagJpaRepository extends JpaRepository<MovieTagEntity, Long> {
  // 특정 유저가 특정 영화에 부여한 태그 리스트 조회
  List<MovieTagEntity> findByUserIdAndMovieId(Long userId, Long movieId);
}
