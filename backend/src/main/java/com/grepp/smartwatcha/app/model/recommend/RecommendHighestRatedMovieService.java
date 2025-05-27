package com.grepp.smartwatcha.app.model.recommend;
import com.grepp.smartwatcha.app.model.recommend.service.highestrated.RecommendHighestRatedJpaService;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
import com.grepp.smartwatcha.app.model.recommend.service.highestrated.RecommendHighestRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecommendHighestRatedMovieService {
    private final RecommendHighestRatedJpaService jpaService;
    private final RecommendHighestRatedNeo4jService neo4jService;

    //JPA로 영화 정보조회, neo4j로 장르 정보 조회하여 결합 후 최종 통합 서비스
    public List<MovieRecommendHighestRatedResponse> getTop10HighestRatedMovies() {
        List<Object[]> movieIdAndScores = jpaService.getTop10MovieIdAndScores();

        return movieIdAndScores.stream()
                .map(obj -> {
                    Long movieId = (Long) obj[0];
                    Double score = (Double) obj[1];

                    MovieEntity movie = jpaService.findMovieById(movieId);
                    if (movie == null) return null;

                    List<String> genres = neo4jService.getGenresByMovieId(movieId);
                    return MovieRecommendHighestRatedResponse.from(movie, score, genres);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}