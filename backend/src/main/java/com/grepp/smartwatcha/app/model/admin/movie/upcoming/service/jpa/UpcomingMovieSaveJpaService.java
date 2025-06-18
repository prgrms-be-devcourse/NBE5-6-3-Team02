package com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.UpcomingMovieJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * 공개 예정작 영화 JPA 저장 서비스
 * 영화 정보를 MySQL 데이터베이스에 저장하고 관리
 *
 * 주요 기능:
 * - 영화 정보 신규 저장 및 업데이트
 * - 중복 데이터 처리
 * - 영화 정보 삭제
 * - 미공개 영화 목록 조회
 *
 * 트랜잭션 관리:
 * - jpaTransactionManager 를 사용하여 트랜잭션 관리
 * - 저장/수정/삭제 작업은 트랜잭션 내에서 실행
 * - 중복 키 예외(DataIntegrityViolationException) 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class UpcomingMovieSaveJpaService {

  private final UpcomingMovieJpaRepository upcomingMovieJpaRepository;

  // 영화 정보를 JPA 엔티티로 변환하여 저장
  // 이미 존재하는 경우 업데이트 수행
  public void saveToJpa(UpcomingMovieDto dto) {
    try {
      boolean exists = upcomingMovieJpaRepository.existsById(dto.getId());

      if (!exists) {
        MovieEntity entity = toJpaEntity(dto);
        upcomingMovieJpaRepository.save(entity);
        log.info("✅ [saveToJpa] 저장 완료: {} (id: {})", dto.getTitle(), dto.getId());
      } else {
        log.info("🔄 [saveToJpa] 기존 영화 업데이트: {} (id: {})", dto.getTitle(), dto.getId());
        updateToJpa(dto);
      }

    } catch (DataIntegrityViolationException e) {
      log.warn("⚠️ [saveToJpa] 중복 저장 시도 감지됨: {} (id: {})", dto.getTitle(), dto.getId());
    }
  }

  // 기존 영화 정보를 업데이트
  // 존재하지 않는 영화 ID로 업데이트 시도 시 로그만 기록
  public void updateToJpa(UpcomingMovieDto dto) {
    upcomingMovieJpaRepository.findById(dto.getId()).ifPresentOrElse(entity -> {
      entity.setTitle(dto.getTitle());
      entity.setReleaseDate(LocalDateTime.parse(dto.getReleaseDate() + "T00:00:00")); // Kotlin에서 날짜 보장
      entity.setOverview(dto.getOverview());
      entity.setCertification(dto.getCertification());
      entity.setPoster(dto.getPosterPath());
      entity.setCountry(dto.getCountry());
      entity.setIsReleased(false); // 동기화 대상은 항상 미공개 상태
      upcomingMovieJpaRepository.save(entity);
      log.info("✅ [saveToJpa] 업데이트 완료: {} (id: {})", dto.getTitle(), dto.getId());
    }, () -> {
      log.warn("❌ [saveToJpa] 존재하지 않는 영화로 업데이트 시도됨: ID={}", dto.getId());
    });
  }

  // 영화 ID로 데이터 삭제
  // 존재하지 않는 ID로 삭제 시도 시 로그만 기록
  public void deleteFromJpaById(Long id) {
    if (upcomingMovieJpaRepository.existsById(id)) {
      upcomingMovieJpaRepository.deleteById(id);
    } else {
      log.warn("❓ [deleteFromJpaById] 삭제 대상 없음: ID={}", id);
    }
  }

  // 현재 시점 이후 개봉 예정인 미공개 영화 목록을 페이지네이션하여 조회
  public Page<MovieEntity> getUpcomingMovies(Pageable pageable) {
    return upcomingMovieJpaRepository.findByIsReleasedFalseAndReleaseDateAfter(LocalDateTime.now(), pageable);
  }

  // Kotlin에서 enrich된 DTO 리스트를 받아 하나씩 JPA 엔티티로 변환 후 저장
  // - 저장 대상: MovieEntity (MySQL)
  // - 개봉일 등 필수 필드는 DTO 기준으로 파싱
  public void saveFromDtos(List<UpcomingMovieDto> dtoList) {
    for (UpcomingMovieDto dto : dtoList) {
      saveToJpa(dto);  // 개별 DTO를 JPA 저장
    }
  }

  // UpcomingMovieDto → JPA MovieEntity 변환
  // - 개봉일은 문자열 형식("yyyy-MM-dd") 기준으로 LocalDateTime 생성
  // - 기본적으로 isReleased = false 설정 (관리자가 별도로 릴리즈 여부 판단)
  private MovieEntity toJpaEntity(UpcomingMovieDto dto) {
    return MovieEntity.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        .poster(dto.getPosterPath())
        .releaseDate(LocalDateTime.parse(dto.getReleaseDate() + "T00:00:00"))
        .overview(dto.getOverview())
        .certification(dto.getCertification())
        .country(dto.getCountry())
        .isReleased(false)
        .build();
  }
}
