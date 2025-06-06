package com.grepp.smartwatcha.app.model.notification.repository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieStatusChangeJpaRepository extends JpaRepository<MovieEntity, Long> {

    @Query("SELECT m FROM MovieEntity m WHERE FUNCTION('DATE', m.releaseDate) <= CURRENT_DATE")
    List<MovieEntity> findByReleaseDateToday();
}
