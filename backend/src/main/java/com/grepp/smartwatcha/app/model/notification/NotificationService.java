package com.grepp.smartwatcha.app.model.notification;

import com.grepp.smartwatcha.app.model.notification.dto.NotificationDto;
import com.grepp.smartwatcha.app.model.notification.service.NotificationJpaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationJpaService notificationJpaService;

    public List<NotificationDto> getActiveNotificationsForUser(Long userId) {
        return notificationJpaService.getActiveNotificationsForUser(userId);
    }

    public void markAsRead(Long id, Long userId) {
        notificationJpaService.markAsRead(id, userId);
    }

    public void deactivateNotification(Long notificationId, Long userId) {
        notificationJpaService.deactivateNotification(notificationId, userId);
    }

    public void markAllAsRead(Long userId) {
        notificationJpaService.markAllAsRead(userId);
    }

    public void deactivateAllNotifications(Long userId) {
        notificationJpaService.deactivateAllNotifications(userId);
    }

    public Long countUnread(Long id) {
        return notificationJpaService.countUnread(id);
    }
}
