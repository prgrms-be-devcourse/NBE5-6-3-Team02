package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieGenreTagResponse;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendPersonalResponse;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendPersonalRatedJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.RecommendPersonalRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendPersonalMovieService {

    private final RecommendPersonalRatedJpaService ratingService;
    private final RecommendPersonalRatedNeo4jService graphService;

    public List<MovieRecommendPersonalResponse> getTop10PersonalMovies(Long userId) {
        List<MovieEntity> allReleasedMovies = ratingService.findAllReleasedMovies();
        List<Long> ratedMovieIdList = ratingService.getRatedMovieIdsByUser(userId);
        List<Long> movieIdList = allReleasedMovies.stream().map(MovieEntity::getId).toList();

        List<MovieGenreTagResponse> genreTagResponseList = graphService.getGenreTagInfoByMovieIdList(movieIdList);
        Map<Long, List<String>> genreMap = genreTagResponseList.stream()
                .collect(Collectors.toMap(MovieGenreTagResponse::getMovieId, MovieGenreTagResponse::getGenres));
        Map<Long, List<String>> tagMap = genreTagResponseList.stream()
                .collect(Collectors.toMap(MovieGenreTagResponse::getMovieId, MovieGenreTagResponse::getTags));

        Map<String, Double> genreScoreMap = ratingService.calculateGenrePreferences(userId, genreMap);
        Map<String, Double> tagScoreMap = ratingService.calculateTagPreferences(userId, tagMap);

        List<AbstractMap.SimpleEntry<MovieEntity, Double>> scored = allReleasedMovies.stream()
                .map(movie -> {
                    List<String> genres = genreMap.getOrDefault(movie.getId(), List.of());
                    List<String> tags = tagMap.getOrDefault(movie.getId(), List.of());

                    double genreAvg = genres.stream().mapToDouble(g -> genreScoreMap.getOrDefault(g, 0.0)).average().orElse(0.0);
                    double tagAvg = tags.stream().mapToDouble(t -> tagScoreMap.getOrDefault(t, 0.0)).average().orElse(0.0);
                    double score = (genreAvg + tagAvg) * 100;

                    return new AbstractMap.SimpleEntry<>(movie, score);
                })
                .filter(e -> !ratedMovieIdList.contains(e.getKey().getId()))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .toList();

        return scored.stream()
                .map(e -> MovieRecommendPersonalResponse.from(
                        e.getKey(),
                        e.getValue(),
                        genreMap.getOrDefault(e.getKey().getId(), List.of()),
                        tagMap.getOrDefault(e.getKey().getId(), List.of())
                ))
                .toList();
    }

}
