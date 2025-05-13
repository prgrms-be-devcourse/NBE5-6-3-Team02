package com.grepp.smartwatcha.app.controller.web.admin;

import com.grepp.smartwatcha.app.model.admin.tag.AdminTagService;
import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import com.grepp.smartwatcha.infra.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

  private final AdminTagService adminTagService;

  @GetMapping
  public String getAllTags(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size,
      @RequestParam(required = false) String keyword, Model model) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<TagEntity> tags = adminTagService.findTagsByKeyword(keyword, pageable);

    PageResponse<TagEntity> pageResponse = new PageResponse<>("/admin/tags", tags, 5);

    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("tags", tags.getContent());
    model.addAttribute("keyword", keyword);

    return "admin/tag/list";
  }
}
