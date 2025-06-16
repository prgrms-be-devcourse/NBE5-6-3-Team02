package com.grepp.smartwatcha.app.controller.web.user;

import com.grepp.smartwatcha.app.controller.web.user.annotation.NonAdmin;
import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.user.dto.*;
import com.grepp.smartwatcha.app.model.user.service.UserJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserJpaService userJpaService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/find-id")
    public String findIdForm() {
        return "user/find-id";
    }

    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "user/find-password";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("signupRequestDto")) {
            model.addAttribute("signupRequestDto", new SignupRequestDto());
        }
        if (!model.containsAttribute("codeSent")) {
            model.addAttribute("codeSent", false);
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequestDto signupRequestDto,
                        Model model, RedirectAttributes redirectAttributes) {
        try {
            userJpaService.signup(signupRequestDto);
            redirectAttributes.addFlashAttribute("message", "Registration completed. Please log in.");
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/find-id")
    public String findId(@ModelAttribute FindIdRequestDto findIdRequestDto, Model model) {
        try {
            String email = userJpaService.findIdByName(findIdRequestDto);
            model.addAttribute("foundEmail", email);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/find-id";
    }

    @PostMapping("/find-password/send-code")
    public String sendPasswordResetCode(@RequestParam String email, Model model) {
        try {
            userJpaService.sendPasswordResetCode(email);
            model.addAttribute("codeSent", true);
            model.addAttribute("email", email);
            model.addAttribute("message", "Verification code has been sent to your email.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/find-password";
    }

    @PostMapping("/find-password/verify")
    public String resetPassword(@ModelAttribute ResetPasswordRequestDto resetPasswordRequestDto,
                              Model model, RedirectAttributes redirectAttributes) {
        try {
            userJpaService.resetPassword(resetPasswordRequestDto);
            redirectAttributes.addFlashAttribute("message", "Password has been successfully changed. Please sign in with your new password.");
            return "redirect:/user/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("codeSent", true);
            model.addAttribute("email", resetPasswordRequestDto.getEmail());
            model.addAttribute("error", e.getMessage());
            return "user/find-password";
        }
    }

    @NonAdmin
    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        UserInfoDto userDto = userJpaService.findUserInfoById(userDetails.getUser().getId());
        model.addAttribute("user", userDto);
        return "user/mypage-info";
    }

    @NonAdmin
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute UserUpdateRequestDto requestDto,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            userJpaService.updateUser(userDetails.getUser().getId(), requestDto);
            redirectAttributes.addFlashAttribute("message", "Profile has been successfully updated.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @NonAdmin
    @PostMapping("/profile/delete")
    public String deleteProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            userJpaService.deleteUser(userDetails.getUser().getId());
            return "redirect:/user/logout";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    @NonAdmin
    @GetMapping("/activity")
    public String userActivity(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUser().getId();
        List<RatedMovieDto> ratedMovies = userJpaService.findRatedMoviesByUserId(userId);
        List<WishlistMovieDto> wishlistMovies = userJpaService.findWishlistMoviesByUserId(userId);
        model.addAttribute("ratedMovies", ratedMovies);
        model.addAttribute("wishlistMovies", wishlistMovies);
        return "user/mypage-activity";
    }
}
