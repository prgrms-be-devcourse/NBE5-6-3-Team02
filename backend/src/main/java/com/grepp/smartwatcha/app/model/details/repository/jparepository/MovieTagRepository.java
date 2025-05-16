package com.grepp.smartwatcha.app.model.details.repository.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieTagRepository extends JpaRepository<MovieTagEntity, Long> {
    boolean existsByUserAndMovieAndTag(UserEntity user, MovieEntity movie, TagEntity tag);
    List<MovieTagEntity> findByUserAndMovie(UserEntity user, MovieEntity movie);
}