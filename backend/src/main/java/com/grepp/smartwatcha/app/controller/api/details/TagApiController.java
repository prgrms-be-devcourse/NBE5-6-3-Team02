package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.TagDto;
import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.TagCountRequestDto;
import com.grepp.smartwatcha.app.model.details.service.TagService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies/{id}/tags")
@RequiredArgsConstructor
public class TagApiController {

    private final TagService tagService;

    @GetMapping("/user")
    public List<String> getUserTags(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId
    ) {
        UserEntity user = userDetails.getUser();
        return tagService.getUserTags(user, movieId)
                .stream()
                .map(tag -> tag.getTag().getName())
                .toList();
    }
    @GetMapping("/search")
    public List<TagDto> searchTags(@RequestParam String keyword) {
        return tagService.searchTags(keyword);
    }

    @PostMapping("/select")
    public void selectTag(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId,
            @RequestParam String tagName
    ) {
        UserEntity user = userDetails.getUser();

        tagService.saveUserTag(user, movieId, tagName);

    }
    @DeleteMapping("/delete")
    public void deleteUserTag(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId,
            @RequestParam String tagName
    ) {
        tagService.deleteUserTag(userDetails.getUser(), movieId, tagName);
    }

    @GetMapping("/top6")
    public List<TagCountRequestDto> top6Tags(@RequestParam Long movieId) {
        return tagService.getTop6Tags(movieId);
    }

}
