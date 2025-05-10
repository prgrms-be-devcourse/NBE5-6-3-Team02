package com.grepp.smartwatcha.app.model.details.service;

import com.grepp.smartwatcha.app.model.details.dto.RatingRequestDto;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.app.model.details.repository.MovieDetailsJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.RatingJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class RatingJpaService {

    private final RatingJpaRepository ratingJpaRepository;
    private final MovieDetailsJpaRepository movieDetailsJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public void addRating(RatingRequestDto dto) {
        MovieEntity movie = movieDetailsJpaRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));
        UserEntity user = userJpaRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        RatingEntity rating = RatingEntity.builder()
                .movie(movie)
                .user(user)
                .score(dto.getScore())
                .build();

        ratingJpaRepository.save(rating);
    }
}
