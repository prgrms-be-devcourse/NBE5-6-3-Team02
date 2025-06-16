package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRecommendLatestJpaRepository extends JpaRepository<MovieEntity, Long> {

    // CreatedAt 기준으로 내림차순 정렬 후 최근 10개 조회
    List<MovieEntity> findTop10ByOrderByCreatedAtDesc();
}