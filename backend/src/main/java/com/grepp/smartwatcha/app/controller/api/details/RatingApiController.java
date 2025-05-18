package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingRequestDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.RatingJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{id}/ratings")
public class RatingApiController {

    private final RatingJpaService ratingJpaService;

    @PostMapping
    public ResponseEntity<String> addRating(
            @PathVariable("id") Long movieId,
            @RequestBody RatingRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        dto.setUserId(userId);
        dto.setMovieId(movieId);

        ratingJpaService.addRating(dto);
        return ResponseEntity.ok("평점이 성공적으로 등록되었습니다.");
    }
    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable("id") Long movieId) {
        double average = ratingJpaService.getAverageRating(movieId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/bars")
    public List<RatingBarDto> getRatingBars(@PathVariable("id") Long movieId) {
        return ratingJpaService.getRatingDistributionList(movieId);
    }

}
