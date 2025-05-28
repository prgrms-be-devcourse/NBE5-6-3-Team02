package com.grepp.smartwatcha.app.model.recommend.repository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieQueryJpaRepository extends JpaRepository<MovieEntity, Long> {

    // 공개된 영화 목록을 조회함
    @Query("SELECT m FROM MovieEntity m WHERE m.isReleased = true")
    List<MovieEntity> findAllReleased();

    // 여러개의 id를 조회
    @Query("SELECT m FROM MovieEntity m WHERE m.id IN :ids")
    List<MovieEntity> findByIdIn(@Param("ids") List<Long> ids);
}