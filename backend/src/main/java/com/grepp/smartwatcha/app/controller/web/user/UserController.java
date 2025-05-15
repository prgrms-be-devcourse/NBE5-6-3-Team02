package com.grepp.smartwatcha.app.controller.web.user;

import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailVerificationRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailCodeVerifyRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.FindIdRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.ResetPasswordRequestDto;
import com.grepp.smartwatcha.app.model.user.service.UserJpaService;
import com.grepp.smartwatcha.app.model.user.service.EmailVerificationJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final EmailVerificationJpaService emailVerificationJpaService;
    private final UserJpaService userJpaService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/user/find-id")
    public String findIdForm() {
        return "user/find-id";
    }

    @GetMapping("/user/find-password")
    public String findPasswordForm() {
        return "user/find-password";
    }

    @GetMapping("/user/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("signupRequestDto")) {
            model.addAttribute("signupRequestDto", new SignupRequestDto());
        }
        if (!model.containsAttribute("codeSent")) {
            model.addAttribute("codeSent", false);
        }
        return "signup";
    }

    @PostMapping("/user/signup/send-code")
    public String sendCode(@ModelAttribute SignupRequestDto signupRequestDto, Model model) {
        try {
            emailVerificationJpaService.sendVerificationCode(
                new EmailVerificationRequestDto(signupRequestDto.getEmail())
            );
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("codeSent", true);
            long remaining = emailVerificationJpaService.getRemainingCooldownTime(signupRequestDto.getEmail());
            System.out.println("DEBUG remainingTime: " + remaining);
            model.addAttribute("remainingTime", remaining);
        } catch (IllegalArgumentException e) {
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("codeSent", true); // 반드시 true로 고정
            long remaining = emailVerificationJpaService.getRemainingCooldownTime(signupRequestDto.getEmail());
            model.addAttribute("remainingTime", remaining);
        }
        return "signup";
    }

    @PostMapping("/user/signup/verify")
    public String verifyAndSignup(@ModelAttribute SignupRequestDto signupRequestDto,
                                  @RequestParam String verificationCode,
                                  Model model, RedirectAttributes redirectAttributes) {
        boolean verified = emailVerificationJpaService.verifyCode(
            new EmailCodeVerifyRequestDto(signupRequestDto.getEmail(), verificationCode)
        );
        if (!verified) {
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("codeSent", true);
            model.addAttribute("error", "인증 코드가 올바르지 않거나 만료되었습니다.");
            return "signup";
        }
        try {
            userJpaService.signup(signupRequestDto);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("codeSent", true);
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/user/find-id")
    public String findId(@ModelAttribute FindIdRequestDto findIdRequestDto, Model model) {
        try {
            String email = userJpaService.findIdByName(findIdRequestDto);
            model.addAttribute("foundEmail", email);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/find-id";
    }

    @PostMapping("/user/find-password/send-code")
    public String sendPasswordResetCode(@RequestParam String email, Model model) {
        try {
            userJpaService.sendPasswordResetCode(email);
            model.addAttribute("codeSent", true);
            model.addAttribute("email", email);
            model.addAttribute("message", "인증 코드가 이메일로 전송되었습니다.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "user/find-password";
    }

    @PostMapping("/user/find-password/verify")
    public String resetPassword(@ModelAttribute ResetPasswordRequestDto resetPasswordRequestDto,
                              Model model, RedirectAttributes redirectAttributes) {
        try {
            userJpaService.resetPassword(resetPasswordRequestDto);
            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다. 새 비밀번호로 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("codeSent", true);
            model.addAttribute("email", resetPasswordRequestDto.getEmail());
            model.addAttribute("error", e.getMessage());
            return "user/find-password";
        }
    }
}
