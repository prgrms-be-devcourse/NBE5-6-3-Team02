package com.grepp.smartwatcha.app.controller.api.details;


import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.InterestJpaService;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movies/{id}/interest")
public class InterestApiController {
    private final InterestJpaService interestJpaService;

    @PostMapping
    public ResponseEntity<Void> setInterestStatus(
            @PathVariable("id") Long movieId,
            @RequestParam Status status,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        if(userDetails == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userEmail = userDetails.getUsername();
        interestJpaService.saveOrUpdateInterest(userEmail, movieId, status);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping
    public ResponseEntity<Void> deleteInterest(@PathVariable("id") Long movieId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        interestJpaService.deleteInterest(userId, movieId);
        return ResponseEntity.noContent().build(); // 204 응답
    }
}

