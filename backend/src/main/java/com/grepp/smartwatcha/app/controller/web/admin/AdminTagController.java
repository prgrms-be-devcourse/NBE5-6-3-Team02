package com.grepp.smartwatcha.app.controller.web.admin;

import com.grepp.smartwatcha.app.model.admin.tag.dto.AdminTagUsageWithUsersDto;
import com.grepp.smartwatcha.app.model.admin.tag.service.AdminMovieTagJpaService;
import com.grepp.smartwatcha.app.model.admin.tag.service.AdminTagJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import com.grepp.smartwatcha.infra.response.PageResponse;
import java.util.Map;
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
public class AdminTagController { // 태그 목록 페이지

  private final AdminTagJpaService adminTagJpaService;
  private final AdminMovieTagJpaService adminMovieTagJpaService;

  // 태그 목록 페이지 반환
  // 입력: page(페이지 번호), size(페이지 크기), keyword(검색 키워드, nullable), model(뷰 모델)
  // 출력: admin/tag/list (태그 목록 뷰)
  @GetMapping
  public String getAllTags(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size,
      @RequestParam(required = false) String keyword, Model model) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<TagEntity> tags = adminTagJpaService.findTagsByKeyword(keyword, pageable);

    PageResponse<TagEntity> pageResponse = new PageResponse<>("/admin/tags", tags, 5);

    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("tags", tags.getContent());
    model.addAttribute("keyword", keyword);

    // 태그별 사용 통계
    Map<Long, Long> tagUsageMap = adminMovieTagJpaService.getTagUsageCountMap(); // ID 기준
    model.addAttribute("tagUsageMap", tagUsageMap);

    // 태그별 사용 유저 + 영화
    Map<Long, AdminTagUsageWithUsersDto> tagUsageDetailsMap = adminMovieTagJpaService.getTagUsageWithUserDetailMap();
    model.addAttribute("tagUsageDetailsMap", tagUsageDetailsMap);

    return "admin/tag/list";
  }
}
