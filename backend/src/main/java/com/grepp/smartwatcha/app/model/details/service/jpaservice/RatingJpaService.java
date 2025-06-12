package com.grepp.smartwatcha.app.model.details.service.jpaservice;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingRequestDto;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.RatingEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieDetailsJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.RatingJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class RatingJpaService {

    private final RatingJpaRepository ratingJpaRepository;
    private final MovieDetailsJpaRepository movieDetailsJpaRepository;
    private final UserJpaRepository userJpaRepository;


    // 해당 유저의 별점 저장 함수
    // 만약 영화의 id와 사용자의 id가 없을 경우 CommonException.BAD_REQUEST 예외 발생
    public void addRating(RatingRequestDto dto) {
        MovieEntity movie = movieDetailsJpaRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new  CommonException(ResponseCode.BAD_REQUEST));
        UserEntity user = userJpaRepository.findById(dto.getUserId())
                .orElseThrow(() -> new  CommonException(ResponseCode.BAD_REQUEST));

        Optional<RatingEntity> optionalRating = ratingJpaRepository.findByUserAndMovie(user, movie);
        RatingEntity rating = optionalRating.orElseGet(RatingEntity::new);
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setScore(dto.getScore());

        if (rating.getCreatedAt() == null) {
            rating.setCreatedAt(LocalDateTime.now());
        }
        ratingJpaRepository.save(rating);
    }


//    영화 ID에 대한 점수별 평가 수를 조회
//    return 점수별 평가 수를  Map 형태로 저장
    public Map<Integer, Integer> getRatingDistribution(Long movieId) {
        List<Object[]> results = ratingJpaRepository.countRatingsByScore(movieId);
        Map<Integer, Integer> distribution = new HashMap<>();

        for (Object[] row : results) {
            Double score = (Double) row[0];
            Long count = (Long) row[1];
            distribution.put(score.intValue(), count.intValue());
        }

        // 누락된 점수는 0으로 채우기
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0);
        }

        return distribution;
    }

    // movieId 에 해당하는 별점을
    // 별점 그래프의 각 1~5까지의 그래프상의 분포도 RatingBarDto로 반환
    public List<RatingBarDto> getRatingDistributionList(Long movieId) {
        Map<Integer, Integer> rawMap = getRatingDistribution(movieId);
        List<RatingBarDto> list = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            list.add(new RatingBarDto(i, rawMap.get(i)));
        }
        return list;
    }

    // 영화의 평균을 반환하는 함수
    public double getAverageRating(Long movieId) {
        Double avg = ratingJpaRepository.getAverageRating(movieId);
        return avg != null ? avg : 0.0;
    }

    // 유저가 특정 영화에 대해 남긴 평점을 반환하는 함수
    public Integer getUserRating(Long userId, Long movieId) {
        return ratingJpaRepository.findRatingByUserAndMovie(userId, movieId)
                .map(r -> r.getScore() != null ? r.getScore().intValue() : null)
                .orElse(null);
    }

    // 유저가 남긴 평점을 삭제하는 로직
    public void deleteRatingByUser(Long userId, Long movieId) {
        ratingJpaRepository.deleteByUserIdAndMovieId(userId, movieId);
    }


}
