package com.grepp.smartwatcha.app.model.details.repository.jparepository;


import com.grepp.smartwatcha.infra.jpa.entity.MovieWatchedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchedJpaRepository extends JpaRepository<MovieWatchedEntity, Integer> {

    // userid, movieid 기반 MovieWatchedEntity에 정보를 가져옴 -> 값을 반환
    Optional<MovieWatchedEntity> findByUserIdAndMovieId(Long userId, Long movieId);

    List<MovieWatchedEntity> findByUserId(Long userId);

    // 삭제 기능
    void deleteByUserIdAndMovieId(Long userId, Long movieId);
}
