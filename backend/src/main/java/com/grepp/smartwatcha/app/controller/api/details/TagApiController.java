package com.grepp.smartwatcha.app.controller.api.details;

import com.grepp.smartwatcha.app.model.details.dto.Neo4jTagDto;
import com.grepp.smartwatcha.app.model.details.service.TagNeo4jService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/movies/{id}/tags")
public class TagApiController {
    private final TagNeo4jService tagService;

    public TagApiController(TagNeo4jService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/select")
    public void selectTag(
            @RequestParam Long userId,
            @RequestParam Long movieId,
            @RequestParam String tagName
    ) {
        tagService.saveTagSelection(userId, movieId, tagName);
    }

    @GetMapping("/top6")
    public List<Neo4jTagDto> top6Tags(@RequestParam Long movieId) {
        return tagService.getTop6Tags(movieId);
    }
}
