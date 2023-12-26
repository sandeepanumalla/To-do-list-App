package com.example.service;

import com.example.response.NotificationDTO;
import org.springframework.stereotype.Service;


public interface MessageBroker {
    void sendNotification(NotificationDTO notificationDto, String notificationType);
}
