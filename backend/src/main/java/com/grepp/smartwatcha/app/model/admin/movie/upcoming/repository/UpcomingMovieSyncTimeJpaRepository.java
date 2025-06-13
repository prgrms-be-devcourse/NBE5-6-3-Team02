package com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository;

import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * 동기화 시간 JPA Repository
 * 영화 정보 동기화 시간의 저장, 조회 기능 제공
 */
public interface UpcomingMovieSyncTimeJpaRepository extends JpaRepository<SyncTimeEntity, String> {
    Optional<SyncTimeEntity> findByType(String type); // 동기화 타입으로 동기화 시간 조회

    // 동기화 타입의 가장 최근 동기화 시간 조회
    // 동기화 시간 기준 내림차순 정렬하여 첫 번째 결과 반환
    Optional<SyncTimeEntity> findTopByTypeOrderBySyncTimeDesc(String type);
}
