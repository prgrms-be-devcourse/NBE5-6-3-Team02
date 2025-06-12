package com.grepp.smartwatcha.app.model.details.repository.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.InterestEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
import com.querydsl.core.group.GroupBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestJpaRepository extends JpaRepository<InterestEntity, Long> {
    Optional<InterestEntity> findByUserAndMovie(UserEntity user, MovieEntity movie);
    List<InterestEntity> findByUserAndStatus(UserEntity user, Status status);

    Optional<InterestEntity> findByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);
}
