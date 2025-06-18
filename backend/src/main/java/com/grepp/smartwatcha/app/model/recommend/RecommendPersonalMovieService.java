package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreDto;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieScoreDto;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieTagDto;
import com.grepp.smartwatcha.app.model.recommend.service.personal.RecommendPersonalRatedJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.personal.RecommendPersonalRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendPersonalMovieService {

    private final RecommendPersonalRatedJpaService jpaService;
    private final RecommendPersonalRatedNeo4jService neo4jService;

    // 사용자의 태그, 장르 기반으로 개인화된 영화 추천
    public List<MovieRecommendResponse> getTop10PersonalMovies(Long userId) {
        List<MovieEntity> allReleasedMovies = jpaService.findAllReleasedMovies();
        List<Long> ratedMovieIds = jpaService.getRatedMovieIdsByUser(userId);
        if (ratedMovieIds.isEmpty()) return List.of();

        List<Long> movieIds = allReleasedMovies.stream()
                .map(MovieEntity::getId)
                .toList();

        Map<Long, List<String>> genreMap = neo4jService.getGenresByMovieIdList(movieIds).stream()
                .collect(Collectors.toMap(MovieGenreDto::getMovieId, MovieGenreDto::getGenres));

        Map<Long, List<String>> tagMap = neo4jService.getTagsByMovieIdList(movieIds).stream()
                .collect(Collectors.toMap(MovieTagDto::getMovieId, MovieTagDto::getTags));

        Map<String, Double> genreScores = jpaService.calculateGenrePreferences(userId, genreMap);
        Map<String, Double> tagScores = jpaService.calculateTagPreferencesByUserOnly(userId, ratedMovieIds);

        List<MovieScoreDto> scored = scoreUnseenMovies(
                allReleasedMovies,
                ratedMovieIds,
                genreMap,
                tagMap,
                genreScores,
                tagScores
        );

        return scored.stream()
                .sorted(Comparator.comparingDouble(MovieScoreDto::getScore).reversed())
                .limit(10)
                .map(dto -> {
                    MovieEntity movie = dto.getMovie();
                    double score = dto.getScore();
                    List<String> genres = genreMap.getOrDefault(movie.getId(), List.of());
                    List<String> tags = tagMap.getOrDefault(movie.getId(), List.of());
                    return MovieRecommendResponse.from(movie, score, genres, tags);
                })
                .toList();
    }
    
    // 아직 평가하지 않은 영화들에 대해 장르,태그 선호도 기반으로 예측 점수 계산
    private List<MovieScoreDto> scoreUnseenMovies(
            List<MovieEntity> allMovies,
            List<Long> ratedMovieIds,
            Map<Long, List<String>> genreMap,
            Map<Long, List<String>> tagMap,
            Map<String, Double> genreScores,
            Map<String, Double> tagScores
    ) {
        List<MovieScoreDto> scored = new ArrayList<>();

        for (MovieEntity movie : allMovies) {
            Long movieId = movie.getId();
            if (ratedMovieIds.contains(movieId)) continue;

            List<String> genres = genreMap.getOrDefault(movieId, List.of());
            List<String> tags = tagMap.getOrDefault(movieId, List.of());

            if (Collections.disjoint(genres, genreScores.keySet()) &&
                    Collections.disjoint(tags, tagScores.keySet())) {
                continue;
            }

            double genreAvg = average(genres, genreScores);
            double tagAvg = average(tags, tagScores);
            double score = (genreAvg + tagAvg) * 100;

            scored.add(new MovieScoreDto(movie, score));
        }

        return scored;
    }

    // 장르, 태그에 대해 해당 항목들의 선호도 평균값 계산
    private double average(List<String> keys, Map<String, Double> scoreMap) {
        return keys.stream()
                .mapToDouble(k -> scoreMap.getOrDefault(k, 0.0))
                .average()
                .orElse(0.0);
    }
}