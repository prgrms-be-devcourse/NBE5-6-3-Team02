package com.grepp.smartwatcha.app.controller.api.notification;

import com.grepp.smartwatcha.app.model.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    @PostMapping("read")
    @ResponseBody
    public void markAsRead(
            @RequestParam("id") Long notificationId,
            @RequestParam("user") Long userId) {
        notificationService.markAsRead(notificationId, userId);
    }

    @PostMapping("delete")
    @ResponseBody
    public void deleteNotification(
            @RequestParam("id") Long notificationId,
            @RequestParam("user") Long userId) {
        notificationService.deactivateNotification(notificationId, userId);
    }

    @PostMapping("readAll")
    @ResponseBody
    public void markAllAsRead(@RequestParam("user") Long userId) {
        notificationService.markAllAsRead(userId);
    }

    @PostMapping("deleteAll")
    @ResponseBody
    public void deleteAllNotifications(@RequestParam("user") Long userId) {
        notificationService.deactivateAllNotifications(userId);
    }
}
