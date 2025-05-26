package com.grepp.smartwatcha.app.model.recommend.service.highestrated;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.app.model.recommend.repository.MovieQueryJpaRepository;
import com.grepp.smartwatcha.app.model.recommend.repository.RecommendHighestRatedMovieJpaRepository;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendHighestRatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecommendHighestRatedJpaService {

    private final RecommendHighestRatedMovieJpaRepository ratingRepo;
    private final MovieQueryJpaRepository movieRepo;
    private final RecommendHighestRatedNeo4jService genreService;


    // 영화 평균 상위 10개의 영화 id와 점수 조회 후 영화 정보 반환
    @Transactional("jpaTransactionManager")
    public List<MovieRecommendHighestRatedResponse> getTop10RatedMovies() {
        Pageable top10 = PageRequest.of(0, 10);
        List<Object[]> rawRatings = ratingRepo.findTop10ByAverageRating(top10);

        return rawRatings.stream()
                .map(this::mapToHighestRatedResponse)
                .filter(Objects::nonNull)
                .toList();
    }

    // 영화 id와 평균 점수 들어 있는 영화 정보 조회 후 neo4j에서 장르 가져와 반환
    private MovieRecommendHighestRatedResponse mapToHighestRatedResponse(Object[] obj) {
        Long movieId = (Long) obj[0];
        Double avgScore = (Double) obj[1];

        MovieEntity movie = movieRepo.findById(movieId).orElse(null);
        if (movie == null) {
            return null;
        }

        List<String> genres = genreService.getGenresByMovieId(movieId);
        return MovieRecommendHighestRatedResponse.from(movie, avgScore, genres);
    }
}

