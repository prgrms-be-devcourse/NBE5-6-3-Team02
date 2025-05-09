package com.grepp.smartwatcha.app.service.recommend;


import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.recommend.MovieQueryRepository;
import com.grepp.smartwatcha.infra.jpa.recommend.RecommendHighestRatedMovieJpaRepository;
import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import com.grepp.smartwatcha.infra.response.recommend.MovieRecommendHighestRatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.grepp.smartwatcha.infra.neo4j.recommend.MovieGenreNeo4jRepository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecommendHighestRatedMovieService {

    private final RecommendHighestRatedMovieJpaRepository ratingRepo;
    private final MovieQueryRepository movieRepo;
    private final MovieGenreNeo4jRepository genreRepo;

    @Transactional("neo4jTransactionManager")
    public List<MovieRecommendHighestRatedResponse> getTop10RatedMovies() {
        return ratingRepo.findTop10ByAverageRating().stream()
                .map(obj -> {
                    Long movieId = (Long) obj[0];
                    Double avgScore = (Double) obj[1];
                    MovieEntity movie = movieRepo.findById(movieId);
                    if(movie == null) return null;

                    List<String> genres = genreRepo.findById(movieId)
                            .map(MovieNode::getGenres)
                            .orElse(List.of())
                            .stream()
                            .map(GenreNode::getName)
                            .toList();

                    return MovieRecommendHighestRatedResponse.from(movie, avgScore, genres);
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
