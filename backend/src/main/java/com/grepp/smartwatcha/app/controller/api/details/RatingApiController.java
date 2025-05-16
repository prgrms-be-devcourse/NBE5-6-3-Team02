package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingRequestDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.RatingJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{id}/ratings")
public class RatingApiController {

    private final RatingJpaService ratingJpaService;

    @PostMapping
    public ResponseEntity<String> addRating(
            @PathVariable("id")Long movieId,
            @RequestBody RatingRequestDto dto) {
        dto.setMovieId(movieId);
        dto.setUserId(1L);
        ratingJpaService.addRating(dto);
        return ResponseEntity.ok("평점이 성공적으로 등록되었습니다.");
    }

}
