package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.mapper.UpcomingMovieMapper;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.jpa.UpcomingMovieJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class UpcomingMovieSaveJpaService {

  private final UpcomingMovieJpaRepository upcomingMovieJpaRepository;
  private final UpcomingMovieMapper upcomingMovieMapper;

  public void saveToJpa(UpcomingMovieDto dto) {
    boolean exists = upcomingMovieJpaRepository.existsById(dto.getId());
    //log.info("[saveToJpa] 저장 시도: {} (id: {}), exists? {}", dto.getTitle(), dto.getId(), exists);
    if (!exists) {
      MovieEntity entity = upcomingMovieMapper.toJpaEntity(dto);
      upcomingMovieJpaRepository.save(entity);
    } else {
     // log.info("[saveToJpa] 중복으로 스킵: {} (id: {})", dto.getTitle(), dto.getId());
    }
  }

  public Page<MovieEntity> getUpcomingMovies(Pageable pageable){
    return upcomingMovieJpaRepository.findByIsReleasedFalseAndReleaseDateAfter(LocalDateTime.now(), pageable);
  }
}
