package com.grepp.smartwatcha.app.model.admin.movie.list.service;

import com.grepp.smartwatcha.app.model.admin.movie.list.AdminMovieJpaRepository;
import com.grepp.smartwatcha.app.model.admin.movie.list.code.AdminMovieSpecifications;
import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieListResponseDto;
import com.grepp.smartwatcha.app.model.admin.movie.list.mapper.AdminMovieMapper;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class AdminMovieJpaService {

  private final AdminMovieJpaRepository adminMovieJpaRepository;

  public Page<AdminMovieListResponseDto> findMovieByFilter(
      String keyword,
      Boolean isReleased,
      LocalDate fromDate,
      LocalDate toDate,
      Pageable pageable
  ) {
    Specification<MovieEntity> spec = Specification.where(null);

    // keyword가 날짜 범위 형식이면 releaseDate로 해석
    if (keyword != null && keyword.matches("^\\d{4}-\\d{2}-\\d{2}\\s~\\s\\d{4}-\\d{2}-\\d{2}$")) {
      String[] dates = keyword.split(" ~ ");
      try {
        fromDate = LocalDate.parse(dates[0].trim());
        toDate = LocalDate.parse(dates[1].trim());
      } catch (DateTimeParseException e) {
        // 날짜 파싱 실패 시 무시
      }
    } else {
      // 일반 텍스트인 경우에만 title로 검색
      spec = spec.and(AdminMovieSpecifications.hasTitle(keyword));
    }

    // 공통 조건
    spec = spec
        .and(AdminMovieSpecifications.isReleased(isReleased))
        .and(AdminMovieSpecifications.hasReleaseDateBetween(fromDate, toDate));

    return adminMovieJpaRepository.findAll(spec, pageable)
        .map(AdminMovieMapper::toDto);
  }

  public AdminMovieListResponseDto findMovieById(Long id) {
    MovieEntity movie = adminMovieJpaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
    return AdminMovieMapper.toDto(movie);
  }

  public void save(MovieEntity movie) {
    adminMovieJpaRepository.save(movie);
  }

  public void update(Long id, MovieEntity movie) {
    MovieEntity existing = adminMovieJpaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Movie not found: " + id));

    existing.setTitle(movie.getTitle());
    existing.setReleaseDate(movie.getReleaseDate());
    existing.setCountry(movie.getCountry());
    existing.setCertification(movie.getCertification());
    existing.setOverview(movie.getOverview());
    existing.setPoster(movie.getPoster());
    existing.setIsReleased(movie.getIsReleased());

    adminMovieJpaRepository.save(existing);
  }

  public void deleteById(Long id) {
    MovieEntity movie = adminMovieJpaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Movie not found"));
    adminMovieJpaRepository.delete(movie);
  }
}
