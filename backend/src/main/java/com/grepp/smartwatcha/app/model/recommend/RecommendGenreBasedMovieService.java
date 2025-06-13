package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieGenreCustomNeo4jRepository;
import com.grepp.smartwatcha.app.model.recommend.service.recentgenre.RecommendGenreBasedMovieJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.recentgenre.RecommendGenreBasedMovieNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendGenreBasedMovieService {

    private final RecommendGenreBasedMovieJpaService jpaService;
    private final RecommendGenreBasedMovieNeo4jService neo4jService;
    private final MovieGenreCustomNeo4jRepository movieGenreCustomNeo4jRepository;

    // 최근에 별점 준 영화 장르 기반 추천
    public List<MovieRecommendResponse> recommendMoviesByGenre(Long userId) {
        Long recentMovieId = jpaService.findMostRecentRatedMovieId(userId);
        if (recentMovieId == null) return Collections.emptyList();

        List<String> targetGenres = neo4jService.findGenresByMovieId(recentMovieId);
        if (targetGenres.isEmpty()) return Collections.emptyList();

        List<Long> unratedMovieIds = jpaService.findUnratedMovieIdsByUser(userId);
        if (unratedMovieIds.isEmpty()) return Collections.emptyList();

        List<MovieGenreResponse> genreInfos = movieGenreCustomNeo4jRepository.findOnlyGenresByMovieIdList(unratedMovieIds);
        List<Long> genreMatchedMovieIds = filterMoviesByGenreMatch(genreInfos, targetGenres);
        if (genreMatchedMovieIds.isEmpty()) return Collections.emptyList();

        Map<Long, Double> topMovieScoreMap = jpaService.findTopRatedMovieScoresByIds(genreMatchedMovieIds);
        if (topMovieScoreMap.isEmpty()) return Collections.emptyList();

        List<MovieEntity> topMovies = jpaService.findMoviesByIds(new ArrayList<>(topMovieScoreMap.keySet()));
        Map<Long, List<String>> genreMap = mapGenresByMovieId(genreInfos, topMovieScoreMap.keySet());

        return convertToRecommendResponses(topMovies, topMovieScoreMap, genreMap);
    }

    // 영화 리스트에서 최근 평가 영화와 장르가 겹치는 것만 필터링
    private List<Long> filterMoviesByGenreMatch(List<MovieGenreResponse> infos, List<String> targetGenres) {
        List<Long> matchedIds = new ArrayList<>();
        for (MovieGenreResponse info : infos) {
            for (String genre : info.getGenres()) {
                if (targetGenres.contains(genre)) {
                    matchedIds.add(info.getMovieId());
                    break;
                }
            }
        }
        return matchedIds;
    }

    // 영화 id 기준으로 장르/태그 정보를 구성
    private Map<Long, List<String>> mapGenresByMovieId(List<MovieGenreResponse> infos, Set<Long> targetIds) {
        Map<Long, List<String>> result = new HashMap<>();
        for (MovieGenreResponse info : infos) {
            if (targetIds.contains(info.getMovieId())) {
                result.put(info.getMovieId(), info.getGenres());
            }
        }
        return result;
    }

    // 영화 엔티티, 점수, 장르/태그 정보를 기반으로 응답 DTO 생성
    private List<MovieRecommendResponse> convertToRecommendResponses(
            List<MovieEntity> movies,
            Map<Long, Double> scoreMap,
            Map<Long, List<String>> genreMap
    ) {
        List<MovieRecommendResponse> responses = new ArrayList<>();
        for (MovieEntity movie : movies) {
            Long id = movie.getId();
            Double score = scoreMap.getOrDefault(id, 0.0);
            List<String> genres = genreMap.getOrDefault(id, Collections.emptyList());
            responses.add(new MovieRecommendResponse(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getReleaseDate(),
                    movie.getCountry(),
                    movie.getPoster(),
                    score,
                    genres,
                    Collections.emptyList()
            ));
        }
        return responses;
    }
}