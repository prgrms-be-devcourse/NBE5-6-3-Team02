package com.grepp.smartwatcha.app.controller.web.user;

import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailVerificationRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailCodeVerifyRequestDto;
import com.grepp.smartwatcha.app.model.user.service.UserJpaService;
import com.grepp.smartwatcha.app.model.user.service.EmailVerificationJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserJpaService userJpaService;
    private final EmailVerificationJpaService emailVerificationJpaService;

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(SignupRequestDto requestDto, RedirectAttributes redirectAttributes) {
        try {
            userJpaService.signup(requestDto);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/signup";
        }
    }

    @PostMapping("/email/verification/send")
    @ResponseBody
    public String sendVerificationCode(EmailVerificationRequestDto requestDto) {
        try {
            emailVerificationJpaService.sendVerificationCode(requestDto);
            return "인증 코드가 이메일로 전송되었습니다.";
        } catch (Exception e) {
            return "인증 코드 전송에 실패했습니다: " + e.getMessage();
        }
    }

    @PostMapping("/email/verification/verify")
    @ResponseBody
    public String verifyCode(EmailCodeVerifyRequestDto requestDto) {
        boolean isVerified = emailVerificationJpaService.verifyCode(requestDto);
        return isVerified ? "이메일 인증이 완료되었습니다." : "인증 코드가 올바르지 않거나 만료되었습니다.";
    }
}
