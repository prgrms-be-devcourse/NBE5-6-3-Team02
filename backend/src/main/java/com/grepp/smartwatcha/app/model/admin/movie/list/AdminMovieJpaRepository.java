package com.grepp.smartwatcha.app.model.admin.movie.list;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminMovieJpaRepository extends JpaRepository<MovieEntity, Long>,
    JpaSpecificationExecutor<MovieEntity> {
}
