package com.grepp.smartwatcha.app.controller.web.notification;

import com.grepp.smartwatcha.app.model.auth.CustomUserDetails;
import com.grepp.smartwatcha.app.model.notification.NotificationService;
import com.grepp.smartwatcha.app.model.notification.dto.NotificationDto;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications/{userId}")
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
