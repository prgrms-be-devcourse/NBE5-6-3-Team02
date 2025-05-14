package com.grepp.smartwatcha.app.model.details.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.InterestEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestJpaRepository extends JpaRepository<InterestEntity, Long> {
    Optional<InterestEntity> findByUserAndMovie(UserEntity user, MovieEntity movie);
}
