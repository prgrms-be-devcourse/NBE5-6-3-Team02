package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieTagRepository extends JpaRepository<MovieTagEntity, Long> {

    // 특정 사용자가 태그한 영화 태그 목록 조회
    @Query("""
        SELECT mt.movie.id, t.name
        FROM MovieTagEntity mt
        JOIN TagEntity t ON mt.tag.id = t.id
        WHERE mt.user.id = :userId
        AND mt.movie.id IN :movieIds
    """)
    List<Object[]> findTagNamesByUserAndMovieIds(@Param("userId") Long userId, @Param("movieIds") List<Long> movieIds);
}
