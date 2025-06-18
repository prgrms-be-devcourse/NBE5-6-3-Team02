package com.grepp.smartwatcha.app.controller.web.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grepp.smartwatcha.app.controller.web.user.annotation.NonAdmin;
import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.details.dto.jpadto.WatchedResponseDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.WatchedJpaService;
import com.grepp.smartwatcha.app.model.user.dto.*;
import com.grepp.smartwatcha.app.model.user.service.UserJpaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserJpaService userJpaService;
    // 시청정보 반환하는 service 주입
    private final WatchedJpaService watchedJpaService;

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
    public String userActivity(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) throws JsonProcessingException {
        Long userId = userDetails.getUser().getId();
        List<RatedMovieDto> ratedMovies = userJpaService.findRatedMoviesByUserId(userId);
        List<WishlistMovieDto> wishlistMovies = userJpaService.findWishlistMoviesByUserId(userId);
        List<WatchedResponseDto> calendarMovies = watchedJpaService.getWatchedMoviesForCalendar(userId);

        model.addAttribute("calendarMovies", calendarMovies); // 여기만 넘김
        model.addAttribute("ratedMovies", ratedMovies);
        model.addAttribute("wishlistMovies", wishlistMovies);
        return "user/mypage-activity";
    }

    // --- JSON 요청 지원: 회원가입 인증 코드 발송 ---
    @PostMapping(value = "/signup/send-code", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendSignupVerificationCodeJson(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = body.get("email");
            userJpaService.sendSignupVerificationCode(email); // 회원가입용 메서드 사용
            result.put("message", "Verification code has been sent to your email.");
            result.put("success", true);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("error", e.getMessage());
            result.put("success", false);
            return ResponseEntity.badRequest().body(result);
        }
    }

    // --- JSON 요청 지원: 회원가입 인증 코드 검증 ---
    @PostMapping(value = "/signup/verify", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifySignupCodeJson(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = body.get("email");
            String code = body.get("code");
            boolean verified = userJpaService.verifyEmailCodeWithKotlinApi(email, code);
            result.put("verified", verified);
            if (verified) {
                result.put("message", "Email verification successful.");
            } else {
                result.put("message", "Verification code is invalid or expired.");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("verified", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/signup/send-code")
    public String sendSignupVerificationCode(@ModelAttribute SignupRequestDto signupRequestDto, Model model) {
        // 디버깅 로그 추가
        log.info("sendSignupVerificationCode: name={}, email={}, phoneNumber={}, password={}", 
            signupRequestDto.getName(), signupRequestDto.getEmail(), signupRequestDto.getPhoneNumber(), signupRequestDto.getPassword());
        try {
            userJpaService.sendSignupVerificationCode(signupRequestDto.getEmail());
            model.addAttribute("codeSent", true);
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("message", "Verification code has been sent to your email.");
        } catch (IllegalArgumentException e) {
            // 쿨타임 에러 메시지라면 codeSent=true로!
            if (e.getMessage() != null && e.getMessage().contains("초 후에 다시 전송할 수 있습니다")) {
                model.addAttribute("codeSent", true);
                model.addAttribute("signupRequestDto", signupRequestDto);
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("codeSent", false);
                model.addAttribute("signupRequestDto", signupRequestDto);
                model.addAttribute("error", e.getMessage());
            }
        }
        return "signup";
    }

    @PostMapping("/signup/verify")
    public String verifySignupCode(@ModelAttribute SignupRequestDto signupRequestDto, 
                                 @RequestParam String verificationCode,
                                 Model model, RedirectAttributes redirectAttributes) {
        // 디버깅 로그
        log.info("verifySignupCode: name={}, email={}, phoneNumber={}, password={}, birth={}", 
            signupRequestDto.getName(), signupRequestDto.getEmail(), signupRequestDto.getPhoneNumber(), 
            signupRequestDto.getPassword(), signupRequestDto.getBirth());

        // 값이 하나라도 null이면 에러 안내
        if (signupRequestDto.getName() == null || signupRequestDto.getEmail() == null || 
            signupRequestDto.getPhoneNumber() == null || signupRequestDto.getPassword() == null ||
            signupRequestDto.getBirth() == null) {
            model.addAttribute("codeSent", true);
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("error", "입력값이 누락되었습니다. 처음부터 다시 시도해 주세요.");
            return "signup";
        }
        try {
            boolean verified = userJpaService.verifyEmailCodeWithKotlinApi(signupRequestDto.getEmail(), verificationCode);
            if (verified) {
                // 인증 성공 시 회원가입 진행
                userJpaService.signup(signupRequestDto);
                redirectAttributes.addFlashAttribute("message", "Registration completed. Please log in.");
                return "redirect:/user/login";
            } else {
                model.addAttribute("codeSent", true);
                model.addAttribute("signupRequestDto", signupRequestDto);
                model.addAttribute("error", "Verification code is invalid or expired.");
                return "signup";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("codeSent", true);
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }
}
