package com.grepp.smartwatcha.app.model.details.repository.neo4jrepository;

import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.Neo4jTagDto;
import com.grepp.smartwatcha.infra.neo4j.node.TagNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagNeo4jRepository extends Neo4jRepository<TagNode, String> {

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
    List<Neo4jTagDto> findTop6TagsByMovieId(@Param("movieId") Long movieId);
}
