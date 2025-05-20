package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.JpaTagDto;
import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.Neo4jTagDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.TagJpaService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.TagNeo4jService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies/{id}/tags")
public class TagApiController {
    private final TagNeo4jService tagService;
    private final TagJpaService tagJpaService;

    public TagApiController(TagNeo4jService tagService, TagJpaService tagJpaService) {
        this.tagService = tagService;
        this.tagJpaService = tagJpaService;
    }

    @GetMapping("/user")
    public List<String> getUserTags(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId
    ) {
        UserEntity user = userDetails.getUser();
        return tagJpaService.getUserTags(user, movieId)
                .stream()
                .map(tag -> tag.getTag().getName())
                .toList();
    }
    @GetMapping("/search")
    public List<JpaTagDto> searchTags(@RequestParam String keyword) {
        return tagJpaService.searchTags(keyword);
    }

    @PostMapping("/select")
    public void selectTag(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId,
            @RequestParam String tagName
    ) {
        UserEntity user = userDetails.getUser();
        try {
            tagJpaService.selectTag(user, movieId, tagName);
        } catch (IllegalStateException e) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete")
    public void deleteUserTag(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long movieId,
            @RequestParam String tagName
    ) {
        tagJpaService.deleteUserTag(userDetails.getUser(), movieId, tagName);
    }

    @GetMapping("/top6")
    public List<Neo4jTagDto> top6Tags(@RequestParam Long movieId) {
        return tagService.getTop6Tags(movieId);
    }

}
