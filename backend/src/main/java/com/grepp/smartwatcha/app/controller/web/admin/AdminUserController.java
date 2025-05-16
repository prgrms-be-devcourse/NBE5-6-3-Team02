package com.grepp.smartwatcha.app.controller.web.admin;

import com.grepp.smartwatcha.app.model.admin.user.dto.AdminRatingDto;
import com.grepp.smartwatcha.app.model.admin.user.service.AdminUserJpaService;
import com.grepp.smartwatcha.app.model.admin.user.dto.AdminUserListResponseDto;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserRatingJpaRepository;
import com.grepp.smartwatcha.app.model.admin.user.service.AdminUserRatingJpaService;
import com.grepp.smartwatcha.infra.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {

  private final AdminUserJpaService adminUserJpaService;
  private final AdminUserRatingJpaService adminUserRatingJpaService;

  @GetMapping("/users")
  public String getUserList(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size,
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String role,
      @RequestParam(required = false) Boolean activated,
      Model model) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<AdminUserListResponseDto> users = adminUserJpaService.findUserByFilter(keyword, role, activated, pageable);

    AdminUserListResponseDto selectedUser = null;
    if (id != null) {
      selectedUser = adminUserJpaService.findUserById(id);
    }

    PageResponse<AdminUserListResponseDto> pageResponse = new PageResponse<>("/admin/users", users, 5);

    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("keyword", keyword);
    model.addAttribute("role", role);
    model.addAttribute("activated", activated);

    return "admin/user/list";
  }

  @PostMapping("/users/{id}/update-status")
  public String updateStatus(@PathVariable Long id,
                             @RequestParam(name = "activated", defaultValue = "false") boolean activated,
                             RedirectAttributes redirectAttributes) {
    adminUserJpaService.updateActivationStatus(id, activated);
    return "redirect:/admin/users";
  }

  @GetMapping("/users/ratings")
  public String viewAllRatings(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size,
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      Model model) {

    Sort sort = direction.equalsIgnoreCase("asc") ?
        Sort.by(sortBy != null ? sortBy : "score").ascending() :
        Sort.by(sortBy != null ? sortBy : "score").descending();

    Pageable pageable = PageRequest.of(page, size, sort);

    AdminUserListResponseDto selectedUser = null;
    Long userId = null;

    if(id != null) {
      selectedUser = adminUserJpaService.findUserById(id);
      userId = id;
    } else if (keyword != null && !keyword.isEmpty()) {
      selectedUser = adminUserJpaService.findUserByName(keyword);
      userId = selectedUser.getId();
    }

    Page<AdminRatingDto> ratingPage = adminUserRatingJpaService.getRatings(userId, pageable);
    PageResponse<AdminRatingDto> pageResponse = new PageResponse<>("/admin/users/ratings", ratingPage, 5);

    model.addAttribute("sortBy", sortBy);
    model.addAttribute("direction", direction);
    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("keyword", keyword);

    return "admin/user/ratings";
  }
}
