package com.grepp.smartwatcha.app.model.recommend;

import com.grepp.smartwatcha.app.model.recommend.service.latest.RecommendLatestRatedJpaService;
import com.grepp.smartwatcha.app.controller.api.recommend.payload.MovieRecommendLatestResponse;
import com.grepp.smartwatcha.app.model.recommend.service.latest.RecommendLatestRatedNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendLatestMovieService {

    private final RecommendLatestRatedJpaService jpaService;
    private final RecommendLatestRatedNeo4jService genreService;

    public List<MovieRecommendLatestResponse> getTop10LatestMovies() {
        
        // 최신 영화 10개 조회
        List<MovieEntity> movies = jpaService.getTop10LatestMovies();

        // DTO 결과 담을 리스트
        List<MovieRecommendLatestResponse> responseList = new ArrayList<>();

        // 최신 영화 10개의 평균 별점과, 정보, 장르를 DTO 로 생성
        for (MovieEntity movie : movies) {
            Double avgScore = jpaService.getAverageScore(movie.getId());
            List<String> genres = genreService.getGenresByMovieId(movie.getId());
            MovieRecommendLatestResponse response = MovieRecommendLatestResponse.from(movie, avgScore, genres);
            responseList.add(response);
        }

        return responseList;
    }
}