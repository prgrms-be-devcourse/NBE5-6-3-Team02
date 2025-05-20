package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.mapper.UpcomingMovieMapper;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.jpa.UpcomingMovieJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    try {
      boolean exists = upcomingMovieJpaRepository.existsById(dto.getId());

      if (!exists) {
        MovieEntity entity = upcomingMovieMapper.toJpaEntity(dto);
        upcomingMovieJpaRepository.save(entity);
        log.info("✅ [saveToJpa] 저장 완료: {} (id: {})", dto.getTitle(), dto.getId());
      } else {
        log.info("⚠️ [saveToJpa] 중복 스킵: {} (id: {})", dto.getTitle(), dto.getId());
        updateToJpa(dto);
      }

    } catch (DataIntegrityViolationException e) {
      log.warn("⚠️ [saveToJpa] 중복 저장 시도 감지됨: {} (id: {})", dto.getTitle(), dto.getId());
    }
  }

  public void updateToJpa(UpcomingMovieDto dto) {
    upcomingMovieJpaRepository.findById(dto.getId()).ifPresentOrElse(entity -> {
      entity.setTitle(dto.getTitle());
      entity.setReleaseDate(dto.getReleaseDateTime());
      entity.setOverview(dto.getOverview());
      entity.setCertification(dto.getCertification());
      entity.setPoster(dto.getPosterPath());
      entity.setCountry(dto.getCountry());
      entity.setIsReleased(false); // 동기화 대상은 항상 미공개 상태

      upcomingMovieJpaRepository.save(entity);
      log.info("🆙 [updateToJpa] 업데이트 완료: {} (id: {})", dto.getTitle(), dto.getId());
    }, () -> {
      log.warn("❌ [updateToJpa] 존재하지 않는 영화로 업데이트 시도됨: ID={}", dto.getId());
    });
  }

  public void deleteFromJpaById(Long id) {
    if (upcomingMovieJpaRepository.existsById(id)) {
      upcomingMovieJpaRepository.deleteById(id);
      log.warn("🧹 [deleteFromJpaById] 삭제 완료: ID={}", id);
    } else {
      log.warn("❓ [deleteFromJpaById] 삭제 대상 없음: ID={}", id);
    }
  }

  public Page<MovieEntity> getUpcomingMovies(Pageable pageable){
    return upcomingMovieJpaRepository.findByIsReleasedFalseAndReleaseDateAfter(LocalDateTime.now(), pageable);
  }
}
