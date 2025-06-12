package com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.neo4j;

import com.grepp.smartwatcha.infra.neo4j.node.ActorNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/*
 * 공개 예정작 배우 Neo4j Repository
 * 공개 예정작 영화의 배우 노드 저장, 조회 기능 제공
 * 배우-영화 관계 관리
 */
public interface UpcomingActorNeo4jRepository extends Neo4jRepository<ActorNode, String> {

}
