package com.grepp.smartwatcha.app.model.notification.repository;

import com.grepp.smartwatcha.infra.jpa.entity.InterestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestUserJpaRepository extends JpaRepository<InterestEntity, Long> {

    List<InterestEntity> findByMovieId(Long movieId);
}
