package com.example.service.factory;

import com.example.model.UserNotification;
import com.example.response.NotificationDTO;
import org.springframework.stereotype.Service;

@Service
public class FileUploadedNotificationFactory implements NotificationFactory{

    @Override
    public UserNotification createNotifications(String message) {
        return null;
    }

    @Override
    public NotificationDTO createNotificationDTO(NotificationType notificationType, String message, String recipientUsername) {
        return null;
    }

}
