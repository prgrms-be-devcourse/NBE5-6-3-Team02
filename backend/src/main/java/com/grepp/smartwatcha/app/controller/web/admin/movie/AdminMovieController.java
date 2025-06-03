package com.grepp.smartwatcha.app.controller.web.admin.movie;

import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieListResponse;
import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieUpdateRequest;
import com.grepp.smartwatcha.app.model.admin.movie.list.service.AdminMovieJpaService;
import com.grepp.smartwatcha.infra.response.PageResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// 관리자 페이지 - 영화 관리 컨트롤러
// 기능: 영화 목록, 단건 조회, 생성, 수정, 삭제, 과거 개봉 상태 처리
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMovieController {

  private final AdminMovieJpaService adminMovieJpaService;

  /**
   * 영화 목록 조회 (관리자용)
   * 입력: 페이징, 키워드, 개봉 여부, 개봉일 범위
   * 출력: 영화 페이지 및 선택된 영화(옵션)
   */
  @GetMapping("/movies")
  public String getMovieList(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "6") int size,
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Boolean isReleased,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
      Model model
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").ascending());

    Page<AdminMovieListResponse> movies = adminMovieJpaService.findMovieByFilter(
        keyword, isReleased, fromDate, toDate, pageable
    );

    AdminMovieListResponse selectedMovie = (id != null)
        ? adminMovieJpaService.findMovieById(id)
        : null;

    PageResponse<AdminMovieListResponse> pageResponse =
        new PageResponse<>("/admin/movies", movies, 5);

    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("selectedMovie", selectedMovie);
    model.addAttribute("keyword", keyword);
    model.addAttribute("isReleased", isReleased);
    model.addAttribute("fromDate", fromDate);
    model.addAttribute("toDate", toDate);

    return "admin/movie/list";
  }

  /**
   * 과거 개봉일이 지났지만 아직 isReleased=false 인 영화들 일괄 개봉 처리
   * 트리거 방식: 관리자 수동 버튼 → 비동기 호출
   */
  @PostMapping("/movies/fix-past-release-status")
  public ResponseEntity<Void> fixPastReleaseStatus() {
    adminMovieJpaService.updatePastMoviesToReleased();
    return ResponseEntity.ok().build();
  }

  /**
   * 영화 생성
   * 입력: AdminMovieUpdateRequest
   * 출력: 목록 페이지 리디렉션
   */
  @PostMapping("/movies/create")
  public String createMovie(@ModelAttribute AdminMovieUpdateRequest request) {
    adminMovieJpaService.save(request);
    return "redirect:/admin/movies";
  }

  /**
   * 영화 수정
   * 입력: 영화 ID, AdminMovieUpdateRequest
   * 출력: 목록 페이지 리디렉션
   */
  @PostMapping("/movies/{id}/update")
  public String updateMovie(
      @PathVariable Long id,
      @ModelAttribute AdminMovieUpdateRequest request
  ) {
    adminMovieJpaService.update(id, request);
    return "redirect:/admin/movies";
  }

  /**
   * 영화 삭제
   * 입력: 영화 ID
   * 출력: 204 No Content 응답
   */
  @DeleteMapping("/movies/{id}")
  public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
    adminMovieJpaService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
