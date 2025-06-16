package com.grepp.smartwatcha.app.model.recommend.repository;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MovieGenreCustomNeo4jRepositoryImpl implements MovieGenreCustomNeo4jRepository {

    private final Neo4jClient neo4jClient;

    @Override
    public List<MovieGenreTagResponse> findGenresAndTagsByMovieIdList(List<Long> movieIdList) {
        return new ArrayList<>(
                neo4jClient.query("""
                    UNWIND $movieIdList AS movieId
                    MATCH (m:MOVIE {id: movieId})
                    OPTIONAL MATCH (m)-[:HAS_GENRE]->(g:GENRE)
                    OPTIONAL MATCH (m)-[:HAS_TAG]->(t:TAG)
                    RETURN m.id AS movieId,
                           collect(DISTINCT g.name) AS genres,
                           collect(DISTINCT t.name) AS tags
                """)
                        .bind(movieIdList).to("movieIdList")
                        .fetchAs(MovieGenreTagResponse.class)
                        .mappedBy((typeSystem, record) ->
                                new MovieGenreTagResponse(
                                        record.get("movieId").asLong(),
                                        new ArrayList<>(record.get("genres").asList(org.neo4j.driver.Value::asString)),
                                        new ArrayList<>(record.get("tags").asList(org.neo4j.driver.Value::asString))
                                )
                        )
                        .all()
        );
    }

    public List<MovieGenreResponse> findOnlyGenresByMovieIdList(List<Long> movieIdList) {
        return new ArrayList<>(
                neo4jClient.query("""
                UNWIND $movieIdList AS movieId
                MATCH (m:MOVIE {id: movieId})
                OPTIONAL MATCH (m)-[:HAS_GENRE]->(g:GENRE)
                RETURN m.id AS movieId,
                       collect(DISTINCT g.name) AS genres
            """)
                        .bind(movieIdList).to("movieIdList")
                        .fetchAs(MovieGenreResponse.class)
                        .mappedBy((typeSystem, record) ->
                                new MovieGenreResponse(
                                        record.get("movieId").asLong(),
                                        new ArrayList<>(record.get("genres").asList(org.neo4j.driver.Value::asString))
                                )
                        )
                        .all()
        );
    }
}