package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRecommendJpaRepository extends JpaRepository<RatingEntity, Long> {

    List<RatingEntity> findByUserId(Long userId);

    // 유저가 평가한 영화 중 2편 이상 평가한 다른 유저 id 조회
    @Query("""
        SELECT r.user.id
        FROM RatingEntity r
        WHERE r.movie.id IN :targetMovieIds
        AND r.user.id <> :targetUserId
        GROUP BY r.user.id
        HAVING COUNT(r.movie.id) >= 2
    """)
    List<Long> findUsersWithCommonRatedMovies(@Param("targetUserId") Long targetUserId,
                                              @Param("targetMovieIds") List<Long> targetMovieIds);

    // 사용자들이 남긴 별점 조회
    List<RatingEntity> findByUserIdIn(List<Long> userIdList);

    Optional<RatingEntity> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}