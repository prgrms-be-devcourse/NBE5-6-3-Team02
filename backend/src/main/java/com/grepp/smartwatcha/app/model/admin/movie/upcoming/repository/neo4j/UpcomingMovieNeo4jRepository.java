package com.grepp.smartwatcha.app.model.admin.movie.upcoming.repository.neo4j;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/*
 * 공개 예정작 영화 Neo4j Repository
 * 공개 예정작 영화 노드의 저장, 조회 기능 제공
 * 영화-배우, 영화-감독, 영화-작가, 영화-장르 관계 관리
 */
public interface UpcomingMovieNeo4jRepository extends Neo4jRepository<MovieNode, Long> {

}
