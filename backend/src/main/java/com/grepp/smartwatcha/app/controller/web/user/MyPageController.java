package com.grepp.smartwatcha.app.controller.web.user;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.user.dto.UserUpdateRequestDto;
import com.grepp.smartwatcha.app.model.user.service.UserJpaService;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserJpaService userJpaService;

    @GetMapping
    public String myPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null && userDetails.getRole() != null && userDetails.getRole().name().equals("ADMIN")) {
            return "redirect:/admin";
        }
        UserEntity user = userJpaService.findById(userDetails.getUser().getId());
        com.grepp.smartwatcha.app.model.user.dto.UserInfoDto userDto = com.grepp.smartwatcha.app.model.user.dto.UserInfoDto.from(user);
        model.addAttribute("user", userDto);
        return "user/mypage-info";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserUpdateRequestDto requestDto,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (userDetails != null && userDetails.getRole() != null && userDetails.getRole().name().equals("ADMIN")) {
            return "redirect:/admin";
        }
        try {
            userJpaService.updateUser(userDetails.getUser().getId(), requestDto);
            redirectAttributes.addFlashAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/mypage";
    }

    @PostMapping("/delete")
    public String deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (userDetails != null && userDetails.getRole() != null && userDetails.getRole().name().equals("ADMIN")) {
            return "redirect:/admin";
        }
        try {
            userJpaService.deleteUser(userDetails.getUser().getId());
            return "redirect:/logout";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/mypage";
        }
    }

    @GetMapping("/activity")
    public String mypageActivity(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null && userDetails.getRole() != null && userDetails.getRole().name().equals("ADMIN")) {
            return "redirect:/admin";
        }
        Long userId = userDetails.getUser().getId();
        var ratedEntities = userJpaService.findRatedMoviesByUserId(userId);
        var wishlistEntities = userJpaService.findWishlistMoviesByUserId(userId);
        var ratedMovies = ratedEntities.stream()
                .map(com.grepp.smartwatcha.app.model.user.dto.RatedMovieDto::from)
                .toList();
        var wishlistMovies = wishlistEntities.stream()
                .map(com.grepp.smartwatcha.app.model.user.dto.WishlistMovieDto::from)
                .toList();
        model.addAttribute("ratedMovies", ratedMovies);
        model.addAttribute("wishlistMovies", wishlistMovies);
        return "user/mypage-activity";
    }
} 