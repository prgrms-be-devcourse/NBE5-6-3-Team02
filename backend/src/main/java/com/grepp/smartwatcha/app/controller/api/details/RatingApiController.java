package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.details.dto.RatingRequestDto;
import com.grepp.smartwatcha.app.model.details.service.RatingJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ratings")
public class RatingApiController {

    private final RatingJpaService ratingJpaService;

    @PostMapping
    public ResponseEntity<String> addRating(@RequestBody RatingRequestDto dto) {
        ratingJpaService.addRating(dto);
        return ResponseEntity.ok("평점이 성공적으로 등록되었습니다.");
    }

}
