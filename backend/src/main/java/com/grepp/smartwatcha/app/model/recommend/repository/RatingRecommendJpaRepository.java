package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieCreatedAtDto;
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

    // 모든 별점 정보 조회
    List<RatingEntity> findByUserId(Long userId);

    // 주어진 영화 2개 이상 평가한 사용자 id조회
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
    
    // 가장 최근 별점 반환
    Optional<RatingEntity> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    // 별점을 MovieCreatedAtResponse DTO로 조회
    @Query("""
        SELECT new com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieCreatedAtDto(
            r.user.id, r.movie.id, r.score, r.createdAt
        )
        FROM RatingEntity r
        WHERE r.user.id IN :userIds
    """)
    List<MovieCreatedAtDto> findRatingProjectionsByUserIdIn(@Param("userIds") List<Long> userIds);

    // 별점을 MovieCreatedAtResponse DTO로 조회
    @Query("""
        SELECT new com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRatingScoreDto(
            r.user.id, r.movie.id, r.score
        )
        FROM RatingEntity r
        WHERE r.user.id IN :userIds
    """)
    List<MovieRatingScoreDto> findRatingScoresByUserIdIn(@Param("userIds") List<Long> userIds);

    // 모든 사람이 태그한 영화의 태그 목록 조회
    @Query("""
        SELECT mt.movie.id, t.name
        FROM MovieTagEntity mt
        JOIN TagEntity t ON mt.tag.id = t.id
        WHERE mt.movie.id IN :movieIds
    """)
    List<Object[]> findTagNamesByMovieIds(@Param("movieIds") List<Long> movieIds);
}