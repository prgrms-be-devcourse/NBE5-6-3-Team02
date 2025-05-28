package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingBarDto;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.RatingRequestDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.RatingJpaService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{id}/ratings")
public class RatingApiController {

    private final RatingJpaService ratingJpaService;

    @PostMapping
    public void addRating(
            @PathVariable("id") Long movieId,
            @RequestBody RatingRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        dto.setUserId(userId);
        dto.setMovieId(movieId);
        if(userId == null || movieId == null) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        ratingJpaService.addRating(dto);
    }
    @DeleteMapping
    public void deleteRating(@PathVariable("id") Long movieId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        if(userId == null){
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        ratingJpaService.deleteRatingByUser(userId, movieId);
    }
    @GetMapping("/average")
    public void getAverageRating(
            @PathVariable("id") Long movieId) {
        ratingJpaService.getAverageRating(movieId);
    }

    @GetMapping("/bars")
    public List<RatingBarDto> getRatingBars(@PathVariable("id") Long movieId) {
        return ratingJpaService.getRatingDistributionList(movieId);
    }


}
