package com.grepp.smartwatcha.app.model.admin.tag.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 관리자 페이지에서 태그 사용 통계 및 유저별 사용 내역을 조회하는 JPA Repository
public interface AdminMovieTagJpaRepository extends JpaRepository<MovieTagEntity, Long> {

  // 전체 태그-영화-유저 관계 조회 (통계용)
  // - 모든 MovieTagEntity를 태그, 영화, 유저와 함께 fetch
  @Query("""
          SELECT mt FROM MovieTagEntity mt
          JOIN FETCH mt.user
          JOIN FETCH mt.movie
          JOIN FETCH mt.tag
      """)
  List<MovieTagEntity> findAllForTagStats();

  // 특정 영화 목록에 대한 태그 사용 내역 조회
  // - 태그, 유저, 영화 정보를 모두 함께 fetch
  @Query("""
        SELECT mt FROM MovieTagEntity mt
        JOIN FETCH mt.user
        JOIN FETCH mt.movie
        JOIN FETCH mt.tag
        WHERE mt.movie.id IN :movieIds
      """)
  List<MovieTagEntity> findAllByMovieIds(@Param("movieIds") List<Long> movieIds);

  // 특정 태그에 대한 유저별 영화 사용 내역 조회
  // - 태그 ID 기준으로 유저, 영화, 태그 관계 fetch
  @Query("""
        SELECT mt FROM MovieTagEntity mt
        JOIN FETCH mt.user
        JOIN FETCH mt.movie
        JOIN FETCH mt.tag
        WHERE mt.tag.id = :tagId
      """)
  List<MovieTagEntity> findAllByTagId(@Param("tagId") Long tagId);
}
