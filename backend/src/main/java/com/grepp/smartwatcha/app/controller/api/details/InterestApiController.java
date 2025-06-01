package com.grepp.smartwatcha.app.controller.api.details;


import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.InterestJpaService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
import com.grepp.smartwatcha.infra.response.ApiResponse;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies/{id}/interest")
public class InterestApiController {
    private final InterestJpaService interestJpaService;

    // 사용자 관심상태 클릭 시 저장 or 해당관심상태로 update 기능
    @PostMapping
    public ResponseEntity<ApiResponse<String>>  setInterestStatus(
            @PathVariable("id") Long movieId,
            @RequestParam Status status,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        String userEmail = userDetails.getUsername();
        interestJpaService.saveOrUpdateInterest(userEmail, movieId, status);
        return ResponseEntity.ok(ApiResponse.success("관심상태가 저장 되었습니다."));
    }

    // 관심상태 중복 선택 시 관심상태 삭제
    // db 에서도 삭제 됨
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteInterest(
            @PathVariable("id") Long movieId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        interestJpaService.deleteInterest(userId, movieId);
        return ResponseEntity.ok(ApiResponse.success("관심 상태가 취소 되었습니다."));
    }
}

