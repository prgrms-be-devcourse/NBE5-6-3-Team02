package com.grepp.smartwatcha.app.controller.web.admin.movie.upcoming;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.UpcomingMovieSyncTimeJpaRepository;
import com.grepp.smartwatcha.app.model.admin.movie.upcoming.service.jpa.UpcomingMovieSaveJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import com.grepp.smartwatcha.infra.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * 공개 예정작 관리 페이지 컨트롤러
 * DB 에서 공개 예정작을 조회하여 관리자 페이지에 표시
 *
 * 입력: 페이지 번호, 페이지 크기
 * 출력: 공개 예정작 목록 페이지 (movie.html)
 *
 * 기능:
 * - 공개 예정작 목록 조회 (개봉일 기준 오름차순 정렬)
 * - 페이지네이션 처리
 * - 마지막 동기화 시간 표시
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/movies/upcoming")
public class UpcomingMoviePageController {

    private final UpcomingMovieSaveJpaService saveJpaService;
    private final UpcomingMovieSyncTimeJpaRepository upcomingMovieSyncTimeJpaRepository;

    /*
     * 공개 예정작 목록 페이지 조회
     *
     * 입력:
     * - page: 페이지 번호 (기본값: 0)
     * - size: 페이지당 항목 수 (기본값: 5)
     *
     * 출력:
     * - movies: 공개 예정작 목록
     * - pageResponse: 페이지네이션 정보
     * - lastSyncTime: 마지막 동기화 시간
     *
     * 정렬:
     * - 개봉일 기준 오름차순 정렬 (최근 개봉작이 아래에 표시)
     */
    @GetMapping
    public String page(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Direction.ASC, "releaseDate"));
        Page<MovieEntity> moviePage = saveJpaService.getUpcomingMovies(pageable);

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("pageResponse", new PageResponse("upcoming", moviePage, 5));
        model.addAttribute("lastSyncTime", upcomingMovieSyncTimeJpaRepository.findByType("upcoming")
            .map(SyncTimeEntity::getSyncTime)
            .orElse(null));

        return "/admin/movie/upcoming/movie";
    }
}
