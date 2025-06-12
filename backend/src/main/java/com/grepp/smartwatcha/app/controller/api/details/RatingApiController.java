package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingRequestDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.RatingJpaService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.ApiResponse;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{id}/ratings")
// 평점 저장 및 삭제 기능
// 그에따른  평균,별점 그래프 즉각 반응 api
public class RatingApiController {

    private final RatingJpaService ratingJpaService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addRating(
            @PathVariable("id") Long movieId,
            @RequestBody RatingRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        dto.setUserId(userId);
        dto.setMovieId(movieId);

        ratingJpaService.addRating(dto);
        return ResponseEntity.ok(ApiResponse.success("평점이 추가 되었습니다."));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteRating(
            @PathVariable("id") Long movieId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        ratingJpaService.deleteRatingByUser(userId, movieId);
        return ResponseEntity.ok(ApiResponse.success("평점이 삭제 되었습니다."));

    }

    @GetMapping("/average")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(
            @PathVariable("id") Long movieId) {
        double average = ratingJpaService.getAverageRating(movieId);
        return ResponseEntity.ok(ApiResponse.success(average));
    }

    @GetMapping("/bars")
    public ResponseEntity<ApiResponse<List<RatingBarDto>>> getRatingBars(@PathVariable("id") Long movieId) {
        List<RatingBarDto> ratingbar = ratingJpaService.getRatingDistributionList(movieId);
        return ResponseEntity.ok(ApiResponse.success(ratingbar));
    }
}
