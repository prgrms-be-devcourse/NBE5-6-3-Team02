package com.grepp.smartwatcha.app.model.admin.tag.repository;

import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageDto;
import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUserUsageDto;
import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// 관리자 페이지에서 태그 사용 통계 및 유저별 사용 내역을 조회하는 JPA Repository
public interface AdminMovieTagJpaRepository extends JpaRepository<MovieTagEntity, Long> {

  // 전체 영화-태그 관계에 대한 태그 사용 통계를 조회
  // @return 태그 ID, 영화 ID, 태그명, 사용 횟수를 포함한 통계 DTO 리스트
  @Query("SELECT new com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageDto(" +
      "mt.tag.id, mt.movie.id, mt.tag.name, COUNT(mt)) " +
      "FROM MovieTagEntity mt " +
      "GROUP BY mt.tag.id, mt.movie.id, mt.tag.name " +
      "ORDER BY COUNT(mt) DESC")
  List<AdminTagUsageDto> findAllTagUsageStats();

  // 특정 태그를 사용한 유저 및 영화 정보를 조회
  // @param tagId 태그 ID
  // * @return 유저 ID, 유저 이름, 영화 ID, 영화 제목을 담은 DTO 리스트
  @Query("SELECT new com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUserUsageDto(mt.tag.id, u.id, u.name, m.id, m.title) " +
      "FROM MovieTagEntity mt " +
      "JOIN mt.user u " +
      "JOIN mt.movie m " +
      "WHERE mt.tag.id = :tagId")
  List<AdminTagUserUsageDto> findUserUsageByTagId(@Param("tagId") Long tagId);

  // 주어진 영화 목록에 대해 태그 사용 통계를 조회
  // @param movieIds 영화 ID 목록
  // * @return 태그 ID, 영화 ID, 태그명, 사용 횟수를 포함한 통계 DTO 리스트
  @Query("SELECT new com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageDto(" +
      "mt.tag.id, mt.movie.id, mt.tag.name, COUNT(mt)) " +
      "FROM MovieTagEntity mt " +
      "WHERE mt.movie.id IN :movieIds " +
      "GROUP BY mt.tag.id, mt.movie.id, mt.tag.name")
  List<AdminTagUsageDto> findTagStatsByMovieIds(@Param("movieIds") List<Long> movieIds);
}
