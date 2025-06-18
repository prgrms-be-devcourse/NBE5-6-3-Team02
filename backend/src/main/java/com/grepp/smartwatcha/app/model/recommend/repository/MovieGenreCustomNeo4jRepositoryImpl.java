package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreDto;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovieGenreCustomNeo4jRepositoryImpl implements MovieGenreCustomNeo4jRepository {

    private final Neo4jClient neo4jClient;

    // 영화의 장르 목록 조회하여 DTO로 반환
    @Override
    public List<MovieGenreDto> findOnlyGenresByMovieIdList(List<Long> movieIdList) {
        return new ArrayList<>(
                neo4jClient.query("""
                MATCH (m:MOVIE)-[:HAS_GENRE]->(g:GENRE)
                WHERE m.id IN $movieIdList
                RETURN m.id AS movieId,
                collect(DISTINCT g.name) AS genres
        """)
                        .bind(movieIdList).to("movieIdList")
                        .fetchAs(MovieGenreDto.class)
                        .mappedBy((typeSystem, record) ->
                                new MovieGenreDto(
                                        record.get("movieId").asLong(),
                                        new ArrayList<>(record.get("genres").asList(org.neo4j.driver.Value::asString))
                                )
                        )
                        .all()
        );
    }

    // 영화의 태그 목록 조회하여 DTO로 반환
    @Override
    public List<MovieTagDto> findTagsByMovieIdList(List<Long> movieIdList) {
        return new ArrayList<>(
                neo4jClient.query("""
                MATCH (m:MOVIE)-[:HAS_TAG]->(t:TAG)
                WHERE m.id IN $movieIdList
                RETURN m.id AS movieId,
                collect(DISTINCT t.name) AS tags
        """)
                        .bind(movieIdList).to("movieIdList")
                        .fetchAs(MovieTagDto.class)
                        .mappedBy((typeSystem, record) ->
                                new MovieTagDto(
                                        record.get("movieId").asLong(),
                                        new ArrayList<>(record.get("tags").asList(org.neo4j.driver.Value::asString))
                                )
                        )
                        .all()
        );
    }
}