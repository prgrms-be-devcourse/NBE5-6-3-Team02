package com.grepp.smartwatcha.app.model.admin.movie.list.service;

import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieListResponse;
import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieUpdateRequest;
import com.grepp.smartwatcha.app.model.admin.movie.list.mapper.AdminMovieMapper;
import com.grepp.smartwatcha.app.model.admin.movie.list.repository.AdminMovieJpaRepository;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class AdminMovieJpaService {

  private final AdminMovieJpaRepository adminMovieJpaRepository;

  // 영화 필터 조회
  // - keyword 가 날짜 범위 형식이면 releaseDate 기준 필터
  // - 그렇지 않으면 title 검색용으로 사용
  // - isReleased, releaseDate 범위, pageable 적용
  public Page<AdminMovieListResponse> findMovieByFilter(
      String keyword,
      Boolean isReleased,
      LocalDate fromDate,
      LocalDate toDate,
      Pageable pageable
  ) {
    // keyword 가 날짜 범위라면 fromDate, toDate로 전환
    if (keyword != null && keyword.matches("^\\d{4}-\\d{2}-\\d{2}\\s~\\s\\d{4}-\\d{2}-\\d{2}$")) {
      String[] dates = keyword.split(" ~ ");
      try {
        fromDate = LocalDate.parse(dates[0].trim());
        toDate = LocalDate.parse(dates[1].trim());

        if (fromDate.isAfter(toDate)) {
          throw new CommonException(ResponseCode.BAD_REQUEST); // 날짜 범위 오류
        }

        keyword = null; // 날짜 범위이면 title 검색 제거
      } catch (DateTimeParseException e) {
        throw new CommonException(ResponseCode.BAD_REQUEST); // 날짜 파싱 오류
      }
    }

    LocalDateTime fromDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null;
    LocalDateTime toDateTime = (toDate != null) ? toDate.plusDays(1).atStartOfDay() : null;

    return adminMovieJpaRepository
        .searchMoviesByConditions(keyword, isReleased, fromDateTime, toDateTime, pageable)
        .map(AdminMovieMapper::toDto);
  }

  // 과거 개봉일이 지났지만 isReleased = false 로 남아있는 영화들을 일괄 released 처리
  public void updatePastMoviesToReleased() {
    List<MovieEntity> unreleased = adminMovieJpaRepository.findPastUnreleasedMovies();

    for (MovieEntity movie : unreleased) {
      movie.setIsReleased(true);
    }

    adminMovieJpaRepository.saveAll(unreleased);
  }

  // 단일 영화 조회 by ID
  public AdminMovieListResponse findMovieById(Long id) {
    MovieEntity movie = adminMovieJpaRepository.findById(id)
        .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));
    return AdminMovieMapper.toDto(movie);
  }

  // 영화 저장
  public boolean save(AdminMovieUpdateRequest request) {
    Long id = request.getId();

    if (adminMovieJpaRepository.existsById(id)) {
      return false;
    }

    MovieEntity movie = AdminMovieMapper.toEntity(request);
    adminMovieJpaRepository.save(movie);
    return true;
  }

  // 영화 정보 수정
  public void update(Long id, AdminMovieUpdateRequest request) {
    MovieEntity existing = adminMovieJpaRepository.findById(id)
        .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));

    MovieEntity updated = AdminMovieMapper.toEntity(request);

    existing.setTitle(updated.getTitle());
    existing.setReleaseDate(updated.getReleaseDate());
    existing.setCountry(updated.getCountry());
    existing.setCertification(updated.getCertification());
    existing.setOverview(updated.getOverview());
    existing.setPoster(updated.getPoster());
    existing.setIsReleased(updated.getIsReleased());

    adminMovieJpaRepository.save(existing);
  }

  // 영화 삭제
  public void deleteById(Long id) {
    MovieEntity movie = adminMovieJpaRepository.findById(id)
        .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));
    adminMovieJpaRepository.delete(movie);
  }
}
