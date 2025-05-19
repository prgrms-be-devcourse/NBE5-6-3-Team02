package com.grepp.smartwatcha.app.controller.web.admin.movie;

import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieListResponseDto;
import com.grepp.smartwatcha.app.model.admin.movie.list.dto.AdminMovieUpdateRequestDto;
import com.grepp.smartwatcha.app.model.admin.movie.list.service.AdminMovieJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMovieController {

  private final AdminMovieJpaService adminMovieJpaService;
  @GetMapping("/movies")
  public String getMovieList(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size,
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Boolean isReleased,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
      Model model
  ){

    Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").ascending());

    Page<AdminMovieListResponseDto> movies = adminMovieJpaService.findMovieByFilter(
        keyword, isReleased, fromDate, toDate, pageable
    );

    AdminMovieListResponseDto selectedMovie = null;
    if (id != null) {
      selectedMovie = adminMovieJpaService.findMovieById(id);
    }

    PageResponse<AdminMovieListResponseDto> pageResponse = new PageResponse<>("/admin/movies", movies, 5);

    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("selectedMovie", selectedMovie);
    model.addAttribute("keyword", keyword);
    model.addAttribute("isReleased", isReleased);
    model.addAttribute("fromDate", fromDate);
    model.addAttribute("toDate", toDate);
    return "admin/movie/list";
  }

  @PostMapping("/movies/create")
  public String createMovie(@ModelAttribute AdminMovieUpdateRequestDto dto){
    MovieEntity movie = dto.toEntity();
    adminMovieJpaService.save(movie);
    return "redirect:/admin/movies";
  }

  @PostMapping("/movies/{id}/update")
  public String updateMovie(
      @PathVariable Long id, @ModelAttribute AdminMovieUpdateRequestDto dto){
    MovieEntity movie = dto.toEntity();
    adminMovieJpaService.update(id, movie);
    return "redirect:/admin/movies";

  }

  @DeleteMapping("/movies/{id}")
  public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
    adminMovieJpaService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
