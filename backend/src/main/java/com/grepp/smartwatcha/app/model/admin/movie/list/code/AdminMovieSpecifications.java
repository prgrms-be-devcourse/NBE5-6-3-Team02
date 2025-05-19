package com.grepp.smartwatcha.app.model.admin.movie.list.code;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import jakarta.persistence.criteria.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.data.jpa.domain.Specification;

public class AdminMovieSpecifications {
  public static Specification<MovieEntity> hasTitle(String title) {
    return(root, query, cb) ->
        (title == null || title.isBlank()) ? null :
            cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  public static Specification<MovieEntity> hasReleaseDateBetween(LocalDate from, LocalDate to) {
    return (root, query, cb) -> {
      Path<LocalDateTime> releaseDate = root.get("releaseDate");

      if (from != null && to != null) {
        LocalDateTime start = from.atStartOfDay(); // 2025-05-21 00:00:00
        LocalDateTime end = to.atTime(LocalTime.MAX); // 2025-05-21 23:59:59.999999999
        return cb.and(
            cb.greaterThanOrEqualTo(releaseDate, start),
            cb.lessThanOrEqualTo(releaseDate, end)
        );
      } else if (from != null) {
        return cb.greaterThanOrEqualTo(releaseDate, from.atStartOfDay());
      } else if (to != null) {
        return cb.lessThanOrEqualTo(releaseDate, to.atTime(LocalTime.MAX));
      } else {
        return null;
      }
    };
  }

  public static Specification<MovieEntity> isReleased(Boolean isReleased) {
    return (root, query, cb) ->
        (isReleased == null) ? null : cb.equal(root.get("isReleased"), isReleased);
  }
}
