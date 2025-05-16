package com.grepp.smartwatcha.app.model.admin.upcoming.mapper;

import com.grepp.smartwatcha.app.model.admin.upcoming.dto.UpcomingMovieDto;
import com.grepp.smartwatcha.app.model.admin.upcoming.service.neo4j.UpcomingMovieGenreFetchNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.neo4j.node.ActorNode;
import com.grepp.smartwatcha.infra.neo4j.node.DirectorNode;
import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import com.grepp.smartwatcha.infra.neo4j.node.WriterNode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpcomingMovieMapper {

  private final UpcomingMovieGenreFetchNeo4jService genreFetchService;

  //mysql
  public MovieEntity toJpaEntity(UpcomingMovieDto dto){
    LocalDateTime releaseDateTime = parseDateWithDefaultTime(dto.getReleaseDate());


    return MovieEntity.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        .poster(dto.getPosterPath())
        .releaseDate(releaseDateTime)
        .overview(dto.getOverview() == null || dto.getOverview().isBlank() ? null : dto.getOverview())
        .country(dto.getCountry())
        .isReleased(releaseDateTime.toLocalDate().isBefore(LocalDate.now()))
        .certification(dto.getCertification())
        .build();
  }

  // 시간 변환 로직
  public LocalDateTime parseDateWithDefaultTime(String datestr){
    return LocalDate.parse(datestr).atStartOfDay();
  }

  //neo4j
  public MovieNode toNeo4jNode(
    UpcomingMovieDto dto,
    List<ActorNode> actors,
    List<DirectorNode> directors,
    List<WriterNode> writers,
    List<GenreNode> genres
  ){
    MovieNode movie = new MovieNode(dto.getId(), dto.getTitle());
    movie.setActors(actors);
    movie.setDirectors(directors);
    movie.setWriters(writers);
    movie.setGenres(genres);

    return movie;
  }
}
