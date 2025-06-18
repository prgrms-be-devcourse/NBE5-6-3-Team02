package com.grepp.smartwatcha.app.model.recommend.service.recenttag;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagDto;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieTagCustomNeo4jRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "neo4jTransactionManager", readOnly = true)
public class RecommendTagBasedMovieNeo4jService {

    private final MovieTagCustomNeo4jRepository movieTagCustomNeo4jRepository;

    // 영화 id에 대해 연결된 태그 리스트 조회 후 반환
    public Map<Long, List<String>> findTagsByMovieIdList(List<Long> movieIds) {
        return movieTagCustomNeo4jRepository.findTagsByMovieIdList(movieIds).stream()
                .collect(Collectors.toMap(MovieTagDto::getMovieId, MovieTagDto::getTags));
    }
}