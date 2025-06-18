package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovieTagCustomNeo4jRepositoryImpl implements MovieTagCustomNeo4jRepository {

    private final Neo4jClient neo4jClient;

    // 영화에 연결된 태그 조회
    @Override
    public List<MovieTagDto> findTagsByMovieIdList(List<Long> movieIdList) {
        return new ArrayList<>(
                neo4jClient.query("""
                UNWIND $movieIdList AS movieId
                MATCH (m:MOVIE {id: movieId})
                OPTIONAL MATCH (m)-[:HAS_TAG]->(t:TAG)
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