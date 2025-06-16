package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendResponse;
import com.grepp.smartwatcha.app.model.recommend.service.recenttag.RecommendTagBasedMovieJpaService;
import com.grepp.smartwatcha.app.model.recommend.service.recenttag.RecommendTagBasedMovieNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        List<MovieEntity> unseenMovies = jpaService.findMoviesNotRatedByUser(userId);

        List<MovieEntity> tagMatchedMovies = new ArrayList<>();
        for (MovieEntity movie : unseenMovies) {
            List<String> movieTags = neo4jService.findTagsByMovieId(movie.getId());
            for (String tag : movieTags) {
                if (targetTags.contains(tag)) {
                    tagMatchedMovies.add(movie);
                    break;
                }
            }
        }

        Map<Long, Double> avgScores = jpaService.calculateAverageScores(tagMatchedMovies);

        List<Map.Entry<Long, Double>> sorted = new ArrayList<>(avgScores.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<MovieRecommendResponse> result = new ArrayList<>();
        int count = 0;
        for (Map.Entry<Long, Double> entry : sorted) {
            if (count++ >= 10) break;
            MovieEntity movie = jpaService.findById(entry.getKey());
            List<String> genres = neo4jService.findGenresByMovieId(movie.getId());
            List<String> tags = neo4jService.findTagsByMovieId(movie.getId());

            result.add(MovieRecommendResponse.from(movie, entry.getValue(), genres, tags));
        }

        System.out.println("targetTags = " + targetTags);
        System.out.println("tagMatchedMovies.size = " + tagMatchedMovies.size());
        for (MovieEntity m : tagMatchedMovies) {
            System.out.println("Matched movie: " + m.getTitle());
        }


        return result;
    }
}