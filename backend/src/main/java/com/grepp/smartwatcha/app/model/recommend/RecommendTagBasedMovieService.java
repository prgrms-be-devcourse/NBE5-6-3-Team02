package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.recommend.service.recenttag.RecommendTagBasedMovieJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.recenttag.RecommendTagBasedMovieNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendTagBasedMovieService {

    private final RecommendTagBasedMovieJpaService jpaService;
    private final RecommendTagBasedMovieNeo4jService neo4jService;

    public List<MovieRecommendResponse> recommendMoviesByTag(Long userId) {
        Long recentMovieId = jpaService.findMostRecentRatedMovieId(userId);
        if (recentMovieId == null) return Collections.emptyList();

        List<String> targetTags = neo4jService.findTagsByMovieId(recentMovieId);
        if (targetTags.isEmpty()) return Collections.emptyList();

        List<Long> unratedMovieIds = jpaService.findUnratedMovieIdsByUser(userId);
        if (unratedMovieIds.isEmpty()) return Collections.emptyList();

        List<MovieTagResponse> tagInfos = neo4jService.findTagsByMovieIdList(unratedMovieIds);
        List<Long> tagMatchedMovieIds = filterMoviesByTagMatch(tagInfos, targetTags);
        if (tagMatchedMovieIds.isEmpty()) return Collections.emptyList();

        Map<Long, Double> topMovieScoreMap = jpaService.findTopRatedMovieScoresByIds(tagMatchedMovieIds);
        if (topMovieScoreMap.isEmpty()) return Collections.emptyList();

        List<MovieEntity> topMovies = jpaService.findMoviesByIds(new ArrayList<>(topMovieScoreMap.keySet()));
        Map<Long, List<String>> tagMap = mapTagsByMovieId(tagInfos, topMovieScoreMap.keySet());

        return convertToRecommendResponses(topMovies, topMovieScoreMap, tagMap);
    }

    private List<Long> filterMoviesByTagMatch(List<MovieTagResponse> infos, List<String> targetTags) {
        List<Long> matchedIds = new ArrayList<>();
        for (MovieTagResponse info : infos) {
            for (String tag : info.getTags()) {
                if (targetTags.contains(tag)) {
                    matchedIds.add(info.getMovieId());
                    break;
                }
            }
        }
        return matchedIds;
    }

    private Map<Long, List<String>> mapTagsByMovieId(List<MovieTagResponse> infos, Set<Long> targetIds) {
        Map<Long, List<String>> result = new HashMap<>();
        for (MovieTagResponse info : infos) {
            if (targetIds.contains(info.getMovieId())) {
                result.put(info.getMovieId(), info.getTags());
            }
        }
        return result;
    }

    private List<MovieRecommendResponse> convertToRecommendResponses(
            List<MovieEntity> movies,
            Map<Long, Double> scoreMap,
            Map<Long, List<String>> tagMap
    ) {
        List<MovieRecommendResponse> responses = new ArrayList<>();
        for (MovieEntity movie : movies) {
            Long id = movie.getId();
            Double score = scoreMap.getOrDefault(id, 0.0);
            List<String> tags = tagMap.getOrDefault(id, Collections.emptyList());

            responses.add(new MovieRecommendResponse(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getReleaseDate(),
                    movie.getCountry(),
                    movie.getPoster(),
                    score,
                    Collections.emptyList(),
                    tags
            ));
        }
        return responses;
    }
}