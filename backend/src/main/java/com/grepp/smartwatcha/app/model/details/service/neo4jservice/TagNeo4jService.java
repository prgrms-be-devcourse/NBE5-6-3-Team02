package com.grepp.smartwatcha.app.model.details.service.neo4jservice;

import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.TagCountRequestDto;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
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

    // 사용자가 특정 영화에 대해 태그를 선택했을 때 관계를 생성
    // 이미 존재하는 사용자/태그 노드가 있을 경우 재사용하며, TAGGED 관계만 생성
    public void saveTagSelection(UserEntity user, Long movieId, String tagName) {
        String query = """
            MERGE (u:USER {id: $userId})
            WITH u
            MATCH (t:TAG {name: $tagName})
            MERGE (u)-[:TAGGED {movieId: $movieId}]->(t)
        """;

        neo4jClient.query(query)
                .bindAll(Map.of(
                        "userId", user.getId(),
                        "movieId", movieId,
                        "tagName", tagName
                ))
                .run();
    }


    // 특정 영화에 대해 가장 많이 선택된 태그 6개를 조회
    // 태그 이름과 선택 횟수를 담은 상위 6개 TagCountRequestDto 리스트로 반환
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

//    사용자-영화-태그 간의 TAGGED 관계를 생성
//    USER, MOVIE, TAG 노드가 모두 없을 경우 생성되며, 관계도 함께 저장
    public void saveTaggedRelation(UserEntity user, Long movieId, String tagName) {
        String query = "MERGE (u:USER {id: $userId}) " +
                "MERGE (m:MOVIE {id: $movieId}) " +
                "MERGE (t:TAG {name: $tagName}) " +
                "MERGE (u)-[:TAGGED {movieId: $movieId}]->(t)";

        neo4jClient.query(query)
                .bindAll(Map.of(
                        "userId", user.getId(),
                        "movieId", movieId,
                        "tagName", tagName
                ))
                .run();
    }

    //사용자-태그 간 특정 영화에 대한 TAGGED 관계를 삭제
    public void deletTaggedRelation(UserEntity user, Long movieId, String tagName) {
        String query = "MATCH (u:USER {id: $userId})-[r:TAGGED {movieId: $movieId}]->(t:TAG {name: $tagName}) DELETE r";
        neo4jClient.query(query)
                .bindAll(Map.of(
                        "userId", user.getId(),
                        "movieId", movieId,
                        "tagName", tagName
                ))
                .run();
    }
}
