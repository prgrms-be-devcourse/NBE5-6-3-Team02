package com.grepp.smartwatcha.app.controller.web.admin.user;

import com.grepp.smartwatcha.app.model.admin.user.service.AdminUserJpaService;
import com.grepp.smartwatcha.app.model.admin.user.dto.AdminUserListResponseDto;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import com.grepp.smartwatcha.infra.response.PageResponse;
import com.grepp.smartwatcha.infra.response.ResponseCode;
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

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
// Admin User List 페이지(유저 목록 및 상태 관리) 컨트롤러
public class AdminUserController {

  private final AdminUserJpaService adminUserJpaService;

  /*
   * 관리자 페이지에서 유저 목록을 조회하는 함수
   * 입력 : page, size, id, keyword(user_name), role, activated (필터링 조건)
   * 출력 : admin/user/list (유저 목록 페이지)
   * 로직 : 필터링 조건에 따라 페이징된 유저 목록을 조회하고, 선택된 유저(id가 있으면)를 조회
   */
  @GetMapping("")
  public String getUserList(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size,
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Role role,
      @RequestParam(required = false) Boolean activated,
      Model model) {

    // 생성일 내림차순으로 정렬된 유저 페이지 조회
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending()); // 가입일 기준으로 desc
    Page<AdminUserListResponseDto> users = adminUserJpaService.findUserByFilter(keyword, role, activated, pageable);

    AdminUserListResponseDto selectedUser = null;

    // 선택된 유저 ID가 있다면 상세 정보 조회
    if (id != null) {
      selectedUser = adminUserJpaService.findUserById(id);
      if(selectedUser == null) {
        // (예외처리) 존재하지 않는 유저 ID인 경우, 명시적으로 던지기
        throw new CommonException(ResponseCode.BAD_REQUEST);
      }
    }

    // 페이지네이션 응답 포맷 생성
    PageResponse<AdminUserListResponseDto> pageResponse = new PageResponse<>("/admin/users", users, 5);

    // 뷰에 데이터 전달
    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("keyword", keyword);
    model.addAttribute("role", role);
    model.addAttribute("activated", activated);

    return "admin/user/list";
  }

  /*
   * 유저의 활성화 상태를 업데이트하는 함수
   * 입력 : id(user_id), activated(활성화 유무)
   * 출력 : redirect:/admin/users(유저 목록 페이지로 리다이렉트)
   * 로직 : AdminUserJpaService.updateActivationStatus 를 호출하여 상태 업데이트
   */
  @PostMapping("/{id}/update-status")
  public String updateStatus(@PathVariable Long id,
                             @RequestParam(name = "activated", defaultValue = "false") boolean activated) {
    adminUserJpaService.updateActivationStatus(id, activated);
    return "redirect:/admin/users";
  }
}
