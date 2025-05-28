package com.grepp.smartwatcha.app.controller.web.notification;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.notification.NotificationJpaService;
import com.grepp.smartwatcha.app.model.notification.dto.NotificationDto;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("notifications")
// 알림 관련 Web 컨트롤러
public class NotificationController {

    private final NotificationJpaService notificationService;

    @GetMapping("")
    /*
     * 기본 진입점
     * 입력: userDetail, Model
     * 출력: redirect:/login, redirect:/notifications/{userId}
     * 로직: userDetail이 없다면 login 페이지로, 있다면 userId 파라미터를 추가해서 redirect
     */
    public String notifications(@AuthenticationPrincipal CustomUserDetails userDetails,Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Long userId = userDetails.getUser().getId();

        return "redirect:/notifications/" + userId;
    }

    @GetMapping("{userId}")
    /*
     * redirect 진입점
     * 입력: userId, userDetail, Model
     * 출력: notification/notification, 403 UNAUTHORIZED
     * 로직: userId와 로그인된 id가 일치하지 않는 다면 403, 일치할 경우 notification 정보를 담아 페이징
     */
    public String getNotifications(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails user,
            Model model) {
        if(user == null || !user.getId().equals(userId)) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        List<NotificationDto> notifications = notificationService.getActiveNotificationsForUser(userId);
        model.addAttribute("notifications", notifications);
        return "notification/notification";
    }
}