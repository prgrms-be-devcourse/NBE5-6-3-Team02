package com.grepp.smartwatcha.app.controller.api.details;


import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.InterestJpaService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.enums.Status;
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

    @PostMapping
    public void setInterestStatus(
            @PathVariable("id") Long movieId,
            @RequestParam Status status,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        if(userDetails == null){
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        String userEmail = userDetails.getUsername();
        interestJpaService.saveOrUpdateInterest(userEmail, movieId, status);
    }
    @DeleteMapping
    public void  deleteInterest(@PathVariable("id") Long movieId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        if(userId == null){
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        interestJpaService.deleteInterest(userId, movieId);
    }
}

