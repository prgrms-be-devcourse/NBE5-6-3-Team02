package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieCreatedAtResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRatingScoreDto;
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

    // 사용자가 남긴 가장 최근 별점 조회
    Optional<RatingEntity> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
    SELECT new com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieCreatedAtResponse(
        r.user.id, r.movie.id, r.score, r.createdAt
    )
    FROM RatingEntity r
    WHERE r.user.id IN :userIds
    """)
    List<MovieCreatedAtResponse> findRatingProjectionsByUserIdIn(@Param("userIds") List<Long> userIds);

    @Query("""
        SELECT new com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRatingScoreDto(
            r.user.id, r.movie.id, r.score
        )
        FROM RatingEntity r
        WHERE r.user.id IN :userIds
    """)
    List<MovieRatingScoreDto> findRatingScoresByUserIdIn(@Param("userIds") List<Long> userIds);
}