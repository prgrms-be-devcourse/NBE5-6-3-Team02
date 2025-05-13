package com.grepp.smartwatcha.app.model.notification;

import com.grepp.smartwatcha.app.model.notification.service.NotificationJpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationJpaService notificationJpaService;

    public void sendNotification(){

    }

}
