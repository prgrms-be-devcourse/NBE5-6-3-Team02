package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.WatchedRequestDto;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.WatchedJpaRepository;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.WatchedJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieWatchedEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{id}/watched")
public class WatchedApiController {

    private final WatchedJpaService watchedJpaService;
    private final WatchedJpaRepository watchedJpaRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> saveWatchedDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId,
            @RequestBody WatchedRequestDto dto
            ){
        watchedJpaService.saveWatchedDate(userDetails.getUser(),movieId,dto.getWatchedDate());
        return ResponseEntity.ok(ApiResponse.success("본 날짜가 추가되었습니다."));
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteWatchedDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId
    ){
        UserEntity user = userDetails.getUser();
        watchedJpaService.deleteWatchedDate(user.getId(),movieId);
        return ResponseEntity.ok(ApiResponse.success("시청 날짜가 삭제 되었습니다."));
    }
}
