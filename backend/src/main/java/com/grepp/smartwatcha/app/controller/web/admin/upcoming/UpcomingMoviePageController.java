package com.grepp.smartwatcha.app.controller.web.admin.upcoming;

import com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.jpa.UpcomingMovieSyncTimeJpaRepository;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/movies/upcoming")
public class UpcomingMoviePageController { // DB 에서 공개예정작을 가져와 화면(movie.html)에 전달

  private final UpcomingMovieSaveJpaService saveJpaService;
  private final UpcomingMovieSyncTimeJpaRepository upcomingMovieSyncTimeJpaRepository;

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
