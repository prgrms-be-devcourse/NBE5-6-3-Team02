package com.grepp.smartwatcha.app.model.details.service.jpaservice;


import com.grepp.smartwatcha.app.model.details.dto.jpadto.WatchedRequestDto;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieDetailsJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.RatingJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.WatchedJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieWatchedEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class WatchedJpaService {

    private final WatchedJpaRepository watchedJpaRepository;
    private final RatingJpaRepository ratingJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final MovieDetailsJpaRepository movieDetailsJpaRepository;

    // 사용자가 남긴 시청 날짜 저장 함수
    public void saveWatchedDate(WatchedRequestDto dto){
        UserEntity user = userJpaRepository.findById(dto.getUserId())
                .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));

        MovieEntity movie = movieDetailsJpaRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));

        // 1. 평점 존재 여부 확인
        boolean hasRating = ratingJpaRepository.existsByUserIdAndMovieId(dto.getUserId(), dto.getMovieId());
        if (!hasRating) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        // Entity에 저장되어있는 정보 꺼내오기
        Optional<MovieWatchedEntity> existing =
                watchedJpaRepository.findByUserIdAndMovieId(dto.getUserId(), dto.getMovieId());

        // 2. 기존 시청 정보 존재 여부 확인
        if (existing.isPresent()) {
            // 존재 시 날짜만 저장
            MovieWatchedEntity entity = existing.get();
            entity.setWatchedDate(dto.getWatchedDate().toLocalDate());
            watchedJpaRepository.save(entity); // 수정 (덮어쓰기)
        } else {
            // 모든 정보 저장
            MovieWatchedEntity entity = new MovieWatchedEntity();
            entity.setUser(user);
            entity.setMovie(movie);
            entity.setWatchedDate(dto.getWatchedDate().toLocalDate());
            watchedJpaRepository.save(entity); // 새로 등록
        }
    }

    // 저장한 날짜를 삭제
    public void deleteWatchedDate(Long userId, Long movieId) {
        watchedJpaRepository.deleteByUserIdAndMovieId(userId, movieId);
    }

}