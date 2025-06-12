package com.grepp.smartwatcha.app.model.notification.dto;

import lombok.Data;

@Data
// 알림 정보 전달을 위한 NotificationDto
public class NotificationDto {

    private Long id;
    private Boolean isRead;
    private String message;

}
