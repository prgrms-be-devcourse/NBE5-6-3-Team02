package com.grepp.smartwatcha.app.controller.web.admin.user;

import com.grepp.smartwatcha.app.model.admin.user.dto.AdminRatingDto;
import com.grepp.smartwatcha.app.model.admin.user.dto.AdminUserListResponse;
import com.grepp.smartwatcha.app.model.admin.user.service.AdminUserJpaService;
import com.grepp.smartwatcha.app.model.admin.user.service.AdminUserRatingJpaService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.PageResponse;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import java.util.List;
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
@RequestMapping("/admin/users/ratings")
@RequiredArgsConstructor
// Admin User Ratings 페이지(유저 평가 관리) 컨트롤러
public class AdminUserRatingController {

  private final AdminUserJpaService adminUserJpaService;
  private final AdminUserRatingJpaService adminUserRatingJpaService;

  /*
   * 관리자 페이지에서 유저의 평가 목록을 조회하는 함수
   * 입력: page, size, id, keyword(user_name), sortBy, direction (정렬 조건)
   * 출력: admin/user/ratings (평가 목록 페이지)
   * 로직: id 또는 keyword(user_name)로 유저를 조회하고, 해당 유저의 평가 목록을 페이징하여 조회
   */
  @GetMapping("")
  public String viewAllRatings(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size,
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String sortBy,
      @RequestParam(defaultValue = "desc") String direction,
      Model model) {

    // 정렬 조건 설정 (기본은 score)
    Sort sort = direction.equalsIgnoreCase("asc") ?
        Sort.by(sortBy != null ? sortBy : "score").ascending() :
        Sort.by(sortBy != null ? sortBy : "score").descending();

    Pageable pageable = PageRequest.of(page, size, sort);

    AdminUserListResponse selectedUser = null;
    Long userId = null;

    // ID로 유저 조회
    if(id != null) {
      selectedUser = adminUserJpaService.findUserById(id);
      if(selectedUser == null) {
        // (예외처리) 존재하지 않는 유저 ID인 경우, 명시적으로 던지기
        throw new CommonException(ResponseCode.BAD_REQUEST);
      }
      userId = id;

      // keyword(이름)로 유저 검색
    } else if (keyword != null && !keyword.isEmpty()) {
      List<AdminUserListResponse> matchedUsers = adminUserJpaService.findUserByName(keyword);
      if(matchedUsers.isEmpty()) {
        // (예외처리) 검색된 유저가 없을 경우, 명시적으로 던지기
        throw new CommonException(ResponseCode.BAD_REQUEST);
      }
      // 검색된 유저 중 첫 번째 유저만 선택하여 처리
      selectedUser = matchedUsers.get(0);
      userId = selectedUser.getId();
    }

    // 평가 데이터 조회
    Page<AdminRatingDto> ratingPage = adminUserRatingJpaService.getRatings(userId, pageable);
    PageResponse<AdminRatingDto> pageResponse = new PageResponse<>("/admin/users/ratings", ratingPage, 5);

    // 뷰에 데이터 전달
    model.addAttribute("sortBy", sortBy);
    model.addAttribute("direction", direction);
    model.addAttribute("pageResponse", pageResponse);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("keyword", keyword);

    return "admin/user/ratings";
  }
}
