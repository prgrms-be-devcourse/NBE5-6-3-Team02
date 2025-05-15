package com.grepp.smartwatcha.app.model.notification.dto;

import lombok.Data;

@Data
public class NotificationDto {

    private Long id;
    private Boolean isRead;
    private String message;

}
