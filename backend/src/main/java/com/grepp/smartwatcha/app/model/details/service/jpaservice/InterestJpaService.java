package com.grepp.smartwatcha.app.model.details.service.jpaservice;

import com.grepp.smartwatcha.app.model.details.repository.jparepository.InterestJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieDetailsJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.InterestEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class InterestJpaService {
    // movie id 기반 영화의 status를 userid에 저장하는 기능 구현
    private final InterestJpaRepository interestJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final MovieDetailsJpaRepository movieDetailsJpaRepository;

    public void saveOrUpdateInterest(String email, Long movieId, Status status) {
        UserEntity user = userJpaRepository.findByEmail(email).orElseThrow();
        MovieEntity movie = movieDetailsJpaRepository.findById(movieId).orElseThrow();
        InterestEntity interestEntity = interestJpaRepository.findByUserAndMovie(user,movie)
                .orElseGet(()->InterestEntity.builder()
                        .user(user)
                        .movie(movie)
                        .build());

        interestEntity.setStatus(status);
        interestJpaRepository.save(interestEntity);
    }


    public Status getInterestStatus(Long userId, Long movieId) {
        return interestJpaRepository.findByUserIdAndMovieId(userId, movieId)
                .map(InterestEntity::getStatus)
                .orElse(null);
    }
    public void deleteInterest(Long userId, Long movieId) {
        interestJpaRepository.deleteByUserIdAndMovieId(userId, movieId);
    }
}
