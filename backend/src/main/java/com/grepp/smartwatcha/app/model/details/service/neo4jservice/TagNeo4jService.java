package com.grepp.smartwatcha.app.model.details.service.neo4jservice;

import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.TagCountRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "neo4jTransactionManager")
public class TagNeo4jService {
    private final Neo4jClient neo4jClient;

    public void saveTagSelection(Long userId, Long movieId, String tagName) {
        String query = """
            MERGE (u:USER {id: $userId})
            WITH u
            MATCH (t:TAG {name: $tagName})
            MERGE (u)-[:TAGGED {movieId: $movieId}]->(t)
        """;

        neo4jClient.query(query)
                .bindAll(Map.of(
                        "userId", userId,
                        "movieId", movieId,
                        "tagName", tagName
                ))
                .run();
    }

    public List<TagCountRequestDto> getTop6Tags(Long movieId) {

        String query = """
            MATCH (:USER)-[r:TAGGED {movieId: $movieId}]->(t:TAG)
            RETURN t.name AS name, count(*) AS count
            ORDER BY count DESC
            LIMIT 6
        """;

        return new ArrayList<>(neo4jClient.query(query)
                .bind(movieId).to("movieId")
                .fetchAs(TagCountRequestDto.class)
                .mappedBy((typeSystem, record) ->
                        new TagCountRequestDto(
                                record.get("name").asString(),
                                record.get("count").asLong()
                        )
                )
                .all());
    }

    public void saveTaggedRelation(Long userId, Long movieId, String tagName) {
        String query = "MERGE (u:USER {id: $userId}) " +
                "MERGE (m:MOVIE {id: $movieId}) " +
                "MERGE (t:TAG {name: $tagName}) " +
                "MERGE (u)-[:TAGGED {movieId: $movieId}]->(t)";

        neo4jClient.query(query)
                .bindAll(Map.of(
                        "userId", userId,
                        "movieId", movieId,
                        "tagName", tagName
                ))
                .run();
    }
    public void deletTaggedRelation(Long userId, Long movieId, String tagName) {
        String query = "MATCH (u:USER {id: $userId})-[r:TAGGED {movieId: $movieId}]->(t:TAG {name: $tagName}) DELETE r";
        neo4jClient.query(query)
                .bindAll(Map.of(
                        "userId", userId,
                        "movieId", movieId,
                        "tagName", tagName
                ))
                .run();
    }
}
