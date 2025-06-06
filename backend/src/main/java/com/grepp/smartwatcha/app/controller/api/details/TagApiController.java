package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.TagDto;
import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.TagCountRequestDto;
import com.grepp.smartwatcha.app.model.details.service.TagService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.response.ApiResponse;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies/{id}/tags")
@RequiredArgsConstructor
public class TagApiController {

    private final TagService tagService;

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<String>>> getUserTags(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId
    ) {
        UserEntity user = userDetails.getUser();
        List<String> userTagNames =  tagService.getUserTags(user, movieId)
                .stream()
                .map(tag -> tag.getTag().getName())
                .toList();
        return ResponseEntity.ok(ApiResponse.success(userTagNames));
    }
    @GetMapping("/search")
    public List<TagDto>searchTags(@RequestParam String keyword) {
        return tagService.searchTags(keyword);
    }

    @PostMapping("/select")
    public ResponseEntity<ApiResponse<String>> selectTag(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId,
            @RequestParam String tagName
    ) {

        UserEntity user = userDetails.getUser();
        tagService.saveUserTag(user, movieId, tagName);
        return ResponseEntity.ok(ApiResponse.success("태그가 등록 되었습니다."));


    }
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteUserTag(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId,
            @RequestParam String tagName
    ) {
        tagService.deleteUserTag(userDetails.getUser(), movieId, tagName);
        return ResponseEntity.ok(ApiResponse.success("태그가 삭제 되었습니다."));
    }

    //사용자가 tag를 남겼을 시 그 tag까지 포함하여 top6Tag를 바로 반환
    @GetMapping("/top6")
    public  ResponseEntity<ApiResponse<List<TagCountRequestDto>>>top6Tags(@RequestParam Long movieId) {
        List<TagCountRequestDto> result =  tagService.getTop6Tags(movieId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

}
