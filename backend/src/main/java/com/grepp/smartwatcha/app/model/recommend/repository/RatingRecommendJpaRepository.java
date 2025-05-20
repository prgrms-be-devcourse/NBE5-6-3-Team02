package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface RatingRecommendJpaRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findByUserId(Long userId);

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

    List<RatingEntity> findByUserIdIn(List<Long> userIdList);
}
