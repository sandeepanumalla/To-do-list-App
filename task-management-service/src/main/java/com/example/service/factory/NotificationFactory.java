package com.example.service.factory;

import com.example.model.UserNotification;
import com.example.response.NotificationDTO;


public interface NotificationFactory {
    UserNotification createNotifications(String message);
    NotificationDTO createNotificationDTO(NotificationType notificationType ,String message, String recipientUsername);
}
