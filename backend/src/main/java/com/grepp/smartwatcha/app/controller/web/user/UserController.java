package com.grepp.smartwatcha.app.controller.web.user;

import com.grepp.smartwatcha.app.model.user.dto.SignupRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailVerificationRequestDto;
import com.grepp.smartwatcha.app.model.user.dto.EmailCodeVerifyRequestDto;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final EmailVerificationJpaService emailVerificationJpaService;
    private final UserJpaService userJpaService;

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

    @PostMapping("/signup/send-code")
    public String sendCode(@ModelAttribute SignupRequestDto signupRequestDto, Model model) {
        try {
            emailVerificationJpaService.sendVerificationCode(
                new EmailVerificationRequestDto(signupRequestDto.getEmail())
            );
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("codeSent", true);
            model.addAttribute("message", "인증 코드가 이메일로 전송되었습니다.");
        } catch (Exception e) {
            model.addAttribute("signupRequestDto", signupRequestDto);
            model.addAttribute("error", "인증 코드 전송에 실패했습니다: " + e.getMessage());
        }
        return "signup";
    }

    @PostMapping("/signup/verify")
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
}
