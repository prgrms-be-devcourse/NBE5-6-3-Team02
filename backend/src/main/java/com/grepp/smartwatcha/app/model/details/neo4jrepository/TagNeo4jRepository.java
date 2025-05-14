package com.grepp.smartwatcha.app.model.details.repository;

import com.grepp.smartwatcha.app.model.details.dto.TagDto;
import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface TagNeo4jRepository extends Neo4jRepository<TagEntity, Long> {

    //유저랑 태그 관계 삽입
    @Query("MERGE (u:USER {id: $userId})\n" +
            "MATCH (t:TAG {name: $tagName})\n" +
            "MERGE (u)-[:TAGGED {movieId: $movieId}]->(t)")
    void createTaggedRelation(
            @Param("userId") Long userId,
            @Param("movieId") Long movieId,
            @Param("tagName") String tagName
    );

    //위 코드로 유저가 선택한 태그를 기반 상위 6개 뽑을 수 있음
    @Query("MATCH (:USER)-[r:TAGGED]->(t:TAG) " +
            "WHERE r.movieId = $movieId " +
            "RETURN t.name AS name, count(*) AS count " +
            "ORDER BY count DESC " +
            "LIMIT 6")
    List<TagDto> findTop6TagsByMovieId(@Param("movieId") Long movieId);
}