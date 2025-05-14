package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.JpaTagDto;
import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.Neo4jTagDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.TagJpaService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.TagNeo4jService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies/{id}/tags")
public class TagApiController {
    private final TagNeo4jService tagService;
    private final TagJpaService tagJpaService;

    public TagApiController(TagNeo4jService tagService, TagJpaService tagJpaService)
    {
        this.tagService = tagService;
        this.tagJpaService = tagJpaService;
    }

    @GetMapping("/search")
    public List<JpaTagDto> searchTags(@RequestParam String keyword){
        return tagJpaService.searchTags(keyword);
    }

    @PostMapping("/select")
    public ResponseEntity<Void> selectTag(
            @RequestParam Long userId,
            @RequestParam Long movieId,
            @RequestParam String tagName
    ) {
        tagService.saveTagSelection(userId, movieId, tagName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top6")
    public List<Neo4jTagDto> top6Tags(@RequestParam Long movieId) {
        return tagService.getTop6Tags(movieId);
    }
}
